package com.steel.product.jswone.service;

import com.steel.product.jswone.request.JSWReportRequest;
import com.steel.product.jswone.request.ReportType;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Single export service for all 7 WMS reports. The report is picked by request.reportType,
 * read from its view with SELECT *, and written column-for-column in view order.
 * Headers come from the view's column labels (snake_case -> "Title Case", acronyms upper-cased),
 * cell types from the JDBC metadata, so a column added to a view shows up without code changes.
 *
 * Rows are streamed from MySQL straight into the sheet (no List in between).
 * NOTE: XSSFWorkbook on purpose. SXSSFWorkbook must not be used on this server.
 */
@Service
public class JSWReportService {

	public static final String XLSX_CONTENT_TYPE =
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private static final ZoneId ZONE = ZoneOffset.UTC;
	private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	private static final int MAX_DATA_ROWS = 1_048_575;   // Excel row limit minus header
	private static final int MAX_CELL_TEXT = 32_767;      // Excel cell text limit

	/** Result handed back to the controller. */
	public static final class ExportFile {
		private final String fileName;
		private final byte[] bytes;
		ExportFile(String fileName, byte[] bytes) { this.fileName = fileName; this.bytes = bytes; }
		public String getFileName() { return fileName; }
		public byte[] getBytes()    { return bytes; }
	}

	private enum Kind { TEXT, INT, DECIMAL, DATE, DATETIME }

	private final NamedParameterJdbcTemplate jdbc;

	public JSWReportService(DataSource dataSource) {
		// Dedicated template so the streaming fetch size doesn't leak into the shared bean.
		JdbcTemplate t = new JdbcTemplate(dataSource);
		t.setFetchSize(Integer.MIN_VALUE);   // MySQL Connector/J: stream rows instead of buffering the full result
		t.setQueryTimeout(300);
		this.jdbc = new NamedParameterJdbcTemplate(t);
	}

	public ExportFile export(JSWReportRequest request) {
		ReportType type = ReportType.from(request.getReportType());

		StringBuilder sql = new StringBuilder("SELECT ").append(type.getFieldNames()).append(" FROM ").append(type.getViewName()).append(" WHERE 1=1");
		MapSqlParameterSource params = new MapSqlParameterSource();

		if (type.getDateColumn() != null) {
			LocalDate[] range = resolveRange(request);
			sql.append(" AND ").append(type.getDateColumn()).append(" BETWEEN :fromDate AND :toDate");
			// 00:00:00 .. 23:59:59 so DATETIME columns include the whole toDate (works for DATE columns too)
			params.addValue("fromDate", java.sql.Timestamp.valueOf(range[0].atStartOfDay()));
			params.addValue("toDate",   java.sql.Timestamp.valueOf(range[1].atTime(23, 59, 59)));
		}

		if (request.getLocationId() > 0 && type.getLocationColumn() != null) {
			sql.append(" AND ").append(type.getLocationColumn()).append(" = :locationId");
			params.addValue("locationId", request.getLocationId());
		}

		sql.append(" ORDER BY ").append(type.getOrderBy());

		byte[] bytes = buildWorkbook(type, sql.toString(), params);
		return new ExportFile(buildFileName(type), bytes);
	}

	public static String buildFileName(ReportType type) {
		return type.getFileBase() + "_" + LocalDateTime.now(ZONE).format(FILE_TS) + ".xlsx";
	}

	/** Both dates inclusive. Defaults: 1st of current month .. today. */
	private static LocalDate[] resolveRange(JSWReportRequest request) {
		LocalDate from = request.getFromDate();
		LocalDate to   = request.getToDate();
		LocalDate today = LocalDate.now(ZONE);
		if (from == null && to == null) { from = today.withDayOfMonth(1); to = today; }
		else if (from == null)          { from = to.withDayOfMonth(1); }
		else if (to == null)            { to = today; }

		if (from.isAfter(to)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fromDate must be on or before toDate");
		}
		return new LocalDate[] { from, to };
	}

	// ---------------------------------------------------------------- workbook

	private byte[] buildWorkbook(ReportType type, String sql, MapSqlParameterSource params) {
		try (XSSFWorkbook wb = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			final Styles styles = new Styles(wb);
			final Sheet sheet = wb.createSheet(type.getSheetName());

			final int[] colCount = {0};
			Integer lastRow = jdbc.query(sql, params, (ResultSetExtractor<Integer>) rs -> {
				ResultSetMetaData md = rs.getMetaData();
				int n = md.getColumnCount();
				colCount[0] = n;

				Kind[] kinds = new Kind[n];
				CellStyle[] numStyles = new CellStyle[n];
				Row header = sheet.createRow(0);
				for (int i = 0; i < n; i++) {
					String label = md.getColumnLabel(i + 1);
					kinds[i] = kindOf(md.getColumnType(i + 1));
					numStyles[i] = (kinds[i] == Kind.DECIMAL && label.toLowerCase(Locale.ROOT).endsWith("_mt"))
							? styles.decimal3 : null;

					String title = prettify(label);
					Cell cell = header.createCell(i);
					cell.setCellValue(title);
					cell.setCellStyle(styles.header);
					sheet.setColumnWidth(i, widthFor(kinds[i], label, title) * 256);
				}

				int r = 1;
				while (rs.next()) {
					if (r > MAX_DATA_ROWS) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Result exceeds Excel row limit; narrow the date range");
					}
					writeRow(sheet.createRow(r++), rs, kinds, numStyles, styles);
				}
				return r - 1;
			});

			if (colCount[0] > 0) {
				sheet.createFreezePane(0, 1);
				sheet.setAutoFilter(new CellRangeAddress(0, Math.max(lastRow == null ? 0 : lastRow, 0), 0, colCount[0] - 1));
			}

			wb.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to build " + type.name() + " export", e);
		}
	}

	private static void writeRow(Row row, ResultSet rs, Kind[] kinds, CellStyle[] numStyles, Styles styles)
			throws SQLException {
		for (int i = 0; i < kinds.length; i++) {
			int col = i + 1;   // JDBC is 1-based
			switch (kinds[i]) {
				case INT: {
					long v = rs.getLong(col);
					if (!rs.wasNull()) {
						Cell cell = row.createCell(i);
						cell.setCellValue(v);
						cell.setCellStyle(styles.integer);
					}
					break;
				}
				case DECIMAL: {
					BigDecimal v = rs.getBigDecimal(col);
					if (v != null) {
						Cell cell = row.createCell(i);
						cell.setCellValue(v.doubleValue());
						if (numStyles[i] != null) cell.setCellStyle(numStyles[i]);
					}
					break;
				}
				case DATE: {
					java.sql.Date v = safeDate(rs, col);
					if (v != null) {
						Cell cell = row.createCell(i);
						cell.setCellValue(v);
						cell.setCellStyle(styles.date);
					}
					break;
				}
				case DATETIME: {
					java.sql.Timestamp v = safeTimestamp(rs, col);
					if (v != null) {
						Cell cell = row.createCell(i);
						cell.setCellValue(v);
						cell.setCellStyle(styles.dateTime);
					}
					break;
				}
				case TEXT:
				default: {
					String v = rs.getString(col);
					if (v != null && !v.isEmpty()) {
						row.createCell(i).setCellValue(v.length() > MAX_CELL_TEXT ? v.substring(0, MAX_CELL_TEXT) : v);
					}
					break;
				}
			}
		}
	}

	/** Legacy rows can hold '0000-00-00'; Connector/J throws on those unless zeroDateTimeBehavior is set. Leave blank. */
	private static java.sql.Date safeDate(ResultSet rs, int col) {
		try { return rs.getDate(col); } catch (SQLException e) { return null; }
	}

	private static java.sql.Timestamp safeTimestamp(ResultSet rs, int col) {
		try { return rs.getTimestamp(col); } catch (SQLException e) { return null; }
	}

	private static Kind kindOf(int sqlType) {
		switch (sqlType) {
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
				return Kind.INT;
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
				return Kind.DECIMAL;
			case Types.DATE:
				return Kind.DATE;
			case Types.TIMESTAMP:
			case Types.TIMESTAMP_WITH_TIMEZONE:
				return Kind.DATETIME;
			default:
				return Kind.TEXT;
		}
	}

	private static int widthFor(Kind kind, String label, String title) {
		switch (kind) {
			case DATETIME: return Math.max(20, Math.min(title.length() + 2, 30));
			case DATE:     return Math.max(13, Math.min(title.length() + 2, 30));
			case INT:
			case DECIMAL:  return Math.max(12, Math.min(title.length() + 2, 24));
			default:
				if (label.toLowerCase(Locale.ROOT).contains("description")) return 40;
				return Math.max(14, Math.min(title.length() + 4, 40));
		}
	}

	// ---------------------------------------------------------------- headers

	/** Whole-label overrides (checked first). */
	private static final Map<String, String> LABELS = new HashMap<>();
	/** Per-word overrides. */
	private static final Map<String, String> WORDS = new HashMap<>();
	static {
		LABELS.put("socreatedate",     "SO Create Date");
		LABELS.put("packet_createdon", "Packet Created On");
		LABELS.put("so_id",            "SO ID");
		LABELS.put("sdi",              "Special Delivery Instructions");
		LABELS.put("likely_mrd",       "Likely MRD");
		LABELS.put("standard_mrd",     "Standard MRD");

		for (String w : new String[] {"so", "sku", "grn", "dc", "wms", "mrd", "uts", "ys", "el", "tc",
									  "po", "fg", "nd", "jw", "wip", "bti", "id", "mmid", "erp", "fifo"}) {
			WORDS.put(w, w.toUpperCase(Locale.ROOT));
		}
		WORDS.put("mt", "(MT)");
		WORDS.put("kg", "(KG)");
		WORDS.put("nos", "(Nos)");
	}

	static String prettify(String label) {
		String key = label.toLowerCase(Locale.ROOT);
		String fixed = LABELS.get(key);
		if (fixed != null) return fixed;

		StringBuilder sb = new StringBuilder();
		for (String part : key.split("_")) {
			if (part.isEmpty()) continue;
			if (sb.length() > 0) sb.append(' ');
			String w = WORDS.get(part);
			sb.append(w != null ? w : Character.toUpperCase(part.charAt(0)) + part.substring(1));
		}
		return sb.toString();
	}

	// ---------------------------------------------------------------- styles

	/** Created once per workbook. Never create styles per cell (64k style limit). */
	private static final class Styles {
		final CellStyle header;
		final CellStyle date;
		final CellStyle dateTime;
		final CellStyle integer;
		final CellStyle decimal3;

		Styles(XSSFWorkbook wb) {
			DataFormat df = wb.createDataFormat();

			Font bold = wb.createFont();
			bold.setBold(true);
			bold.setColor(IndexedColors.WHITE.getIndex());

			header = wb.createCellStyle();
			header.setFont(bold);
			header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			header.setBorderBottom(BorderStyle.THIN);
			header.setWrapText(true);

			date = wb.createCellStyle();
			date.setDataFormat(df.getFormat("dd-mm-yyyy"));

			dateTime = wb.createCellStyle();
			dateTime.setDataFormat(df.getFormat("dd-mm-yyyy hh:mm:ss"));

			integer = wb.createCellStyle();
			integer.setDataFormat(df.getFormat("0"));

			decimal3 = wb.createCellStyle();
			decimal3.setDataFormat(df.getFormat("0.000"));
		}
	}
}