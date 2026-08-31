package com.steel.product.jswone.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.steel.product.jswone.entity.SalesOrderPacketsJswEntity;
import com.steel.product.jswone.entity.StatusType;
import com.steel.product.jswone.repository.SalesOrderChildJswRepository;
import com.steel.product.jswone.repository.SalesOrderJswRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesOrderJswHelperService {

	@Autowired
	private SalesOrderJswRepository salesOrderRepository;

	@Autowired
	private SalesOrderChildJswRepository childRepository;

	@Transactional
	public void updateCPStatus(int soIdValue) {
		log.info("inside updateCPStatus") ;
		try {
			Map<Integer, List<String>> soChildStatusMap = new HashMap<>();
			List<Object[]> list = salesOrderRepository.getAllSODetailsWithAllocationStatusforCPStatus(soIdValue);
			for (Object[] result : list) {
				Object value = result[1];
				Integer soChildId = null;
				if (value != null) {
					if (value instanceof Number) {
						soChildId = ((Number) value).intValue();
					} else {
						soChildId = Integer.parseInt(value.toString());
					}
				}
				String inwardStatus = result[4] != null ? (String) result[4] : null;
				String packetStatus = result[5] != null ? (String) result[5] : null;

				// override logic
				if (packetStatus != null && !packetStatus.isEmpty()) {
					inwardStatus = packetStatus;
				}

				if (soChildId != null) {
					soChildStatusMap.computeIfAbsent(soChildId, k -> new ArrayList<>()).add(inwardStatus);
				}
			}
			Map<Integer, String> finalStatusMap = new HashMap<>();

			for (Map.Entry<Integer, List<String>> entry : soChildStatusMap.entrySet()) {
				List<String> statuses = entry.getValue();
				boolean allReceived = true;
				boolean allWip = true;
				boolean allReady = true;
				boolean allDispatched = true;

				for (String st : statuses) {
					if (!"RECEIVED".equalsIgnoreCase(st)) {
						allReceived = false;
					}
					if (!"IN PROGRESS".equalsIgnoreCase(st)) {
						allWip = false;
					}
					if (!"READY TO DELIVER".equalsIgnoreCase(st)) {
						allReady = false;
					}
					if (!"DISPATCHED".equalsIgnoreCase(st)) {
						allDispatched = false;
					}
				}

				String finalStatus = "RECEIVED";
				if (allDispatched) {
					finalStatus = "DISPATCHED";
				} else if (allReady) {
					finalStatus = "READY TO DELIVER";
				} else if (allWip) {
					finalStatus = "IN PROGRESS";
				} else if (allReceived) {
					finalStatus = "RECEIVED";
				} else {
					finalStatus = "RECEIVED";
				}
				finalStatusMap.put(entry.getKey(), finalStatus);
			}

			for (Map.Entry<Integer, String> entry : finalStatusMap.entrySet()) {
				String status = entry.getValue();
				int soChildId = entry.getKey();
				childRepository.updateItemStataus("Allocated - " + status, soChildId);
			}

			List<SalesOrderPacketsJswEntity> allocations = childRepository.findBySoId_SoId(soIdValue);

			boolean allAllocated = allocations.stream().allMatch(a -> a.getSoqty() != null
					&& a.getAllocatedSoqty() != null && a.getAllocatedSoqty().compareTo(a.getSoqty()) >= 0);
			if (allAllocated) {
				salesOrderRepository.updateCPStataus(StatusType.CP_PLAN_ISSUED.toString(), soIdValue);
			} else {
				salesOrderRepository.updateCPStataus(StatusType.CP_PLAN_DRAFT.toString(), soIdValue);
			}

			List<Object[]> allocations1 = salesOrderRepository.getAllSODetailsWithPacketStatus(soIdValue);
			Map<Integer, List<String>> soStatusMap = new HashMap<>();
			for (Object[] result : allocations1) {

				// --- Safe ID extraction ---
				Integer soIdDummy = null;
				Object value = result[0];

				if (value instanceof Number) {
					soIdDummy = ((Number) value).intValue();
				} else if (value != null) {
					soIdDummy = Integer.parseInt(value.toString());
				}

				// --- Safe string extraction ---
				String item_so_status = result[3] != null ? result[3].toString() : null;

				// --- Populate map ---
				if (soIdDummy != null && item_so_status != null) {
					soStatusMap.computeIfAbsent(soIdDummy, k -> new ArrayList<>()).add(item_so_status);
				}
			}

			Map<Integer, String> finalSOStatusMap = new HashMap<>();

			for (Map.Entry<Integer, List<String>> entry : soStatusMap.entrySet()) {

				List<String> statuses = entry.getValue();

				boolean allocated = true;
				boolean allocatedPartially = true;
				boolean allocatedReceived = true;
				boolean allocatedInProgress = true;
				boolean allocatedReadyToDeliver = true;
				boolean allocatedDispatched = true;

				for (String st : statuses) {
					if (!"Allocated".equalsIgnoreCase(st)) {
						allocated = false;
					}
					if (!"Allocated - Partially".equalsIgnoreCase(st)) {
						allocatedPartially = false;
					}
					if (!"Allocated - RECEIVED".equalsIgnoreCase(st)) {
						allocatedReceived = false;
					}
					if (!"Allocated - IN PROGRESS".equalsIgnoreCase(st)) {
						allocatedInProgress = false;
					}
					if (!"Allocated - READY TO DELIVER".equalsIgnoreCase(st)) {
						allocatedReadyToDeliver = false;
					}
					if (!"Allocated - DISPATCHED".equalsIgnoreCase(st)) {
						allocatedDispatched = false;
					}
				}

				String finalStatus = StatusType.CP_PLAN_DRAFT.toString();
				if (allocated || allocatedPartially) {
					finalStatus = StatusType.CP_PLAN_ISSUED.toString();
				} else if (allocatedReceived) {
					finalStatus = StatusType.CP_PLAN_INPROGRESS.toString();
				} else if (allocatedInProgress) {
					finalStatus = StatusType.CP_PLAN_INPROGRESS.toString();
				} else if (allocatedReadyToDeliver) {
					finalStatus = StatusType.CP_PLAN_INPROGRESS.toString();
				} else if (allocatedDispatched) {
					finalStatus = StatusType.CP_PLAN_COMPLETED.toString();
				} else {
					finalStatus = StatusType.CP_PLAN_DRAFT.toString();
				}
				finalSOStatusMap.put(entry.getKey(), finalStatus);
			}

			for (Map.Entry<Integer, String> entry : finalSOStatusMap.entrySet()) {
				String finalCPStatus = entry.getValue();
				int soId11 = entry.getKey();
				System.out.println("SoId == " + soId11 + ", finalCPStatus== " + finalCPStatus);
				salesOrderRepository.updateCPStataus(finalCPStatus, soId11);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void updateSOStatus(int soIdValue) {
		try {
			List<Object[]> rows = salesOrderRepository.getAllSODetailsWithAllocationStatusforSOStatus(soIdValue);

			Map<Integer, List<String>> soStatusMap = buildSoStatusMap(rows);

			Map<Integer, String> finalSOStatusMap = resolveFinalSoStatus(soStatusMap);

			for (Map.Entry<Integer, String> entry : finalSOStatusMap.entrySet()) {
				Integer soId = entry.getKey();
				String status = entry.getValue();
				System.out.println("SOStataus == " + status + ", soId == " + soId);
				salesOrderRepository.updateSOStataus(status, soId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<Integer, List<String>> buildSoStatusMap(List<Object[]> rows) {

		Map<Integer, List<String>> map = new HashMap<>();

		for (Object[] result : rows) {

			Integer soId = parseInt(result[0]);
			Integer soChildId = parseInt(result[1]);

			if (soId == null || soChildId == null) {
				continue;
			}

			String inwardStatus = safeString(result[4]);
			String packetStatus = safeString(result[5]);

			int instructionsId = parseIntValue(result[7]);
			int formId = parseIntValue(result[8]);
			String itemSOStatus = safeString(result[9]);
			 
			String status;

			if (instructionsId > 0) {
				status = packetStatus;
			} else {
				status = (formId != 21) ? "RM" : inwardStatus;
			}
			System.out.println("status == " + status + ", soId == " + soId + ", soChildId == " + soChildId  +", itemSOStatus == "+itemSOStatus);

			map.computeIfAbsent(soId, k -> new ArrayList<>()).add(status);
		}

		return map;
	}

	private Integer parseInt(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}

		return Integer.parseInt(obj.toString());
	}

	private int parseIntValue(Object obj) {
		Integer val = parseInt(obj);
		return val != null ? val : 0;
	}

	private String safeString(Object obj) {
		return obj != null ? obj.toString() : null;
	}

	private Map<Integer, String> resolveFinalSoStatus(Map<Integer, List<String>> soStatusMap) {

		Map<Integer, String> result = new HashMap<>();

		for (Map.Entry<Integer, List<String>> entry : soStatusMap.entrySet()) {

			Integer soId = entry.getKey();
			List<String> statuses = entry.getValue();

			int minRank = statuses.stream().mapToInt(this::getStatusRank).min().orElse(0);

			String finalStatus = getStatusFromRank(minRank);

			result.put(soId, finalStatus);

			System.out.println("SO ID = " + soId + " FINAL STATUS = " + finalStatus);
		}

		return result;
	}

	private int getStatusRank(String status) {

		if (status == null)
			return 0;

		switch (status.toUpperCase()) {

		case "DISPATCHED":
			return 5;

		case "READY TO DELIVER":
			return 4;

		case "IN PROGRESS":
			return 3;

		case "RECEIVED":
			return 2;

		case "RM":
		case "PENDING_PLAN":
			return 1;

		default:
			return 0; // SO_CREATED
		}
	}

	private String getStatusFromRank(int rank) {
		switch (rank) {
		case 5:
			return "FULFILLED";
		case 4:
			return "PENDING_DELIVERY";
		case 3:
			return "PENDING_ALLOCATION";
		case 2:
			return "PENDING_ALLOCATION";
		case 1:
			return "PENDING_PLAN";
		default:
			return "SO_CREATED";
		}
	}

}
