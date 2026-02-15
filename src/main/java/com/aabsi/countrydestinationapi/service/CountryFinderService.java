package com.aabsi.countrydestinationapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author HP
 */
@Slf4j
@Service
public class CountryFinderService {

	@Autowired
	private CountryPopularService countryPopularService;

	public ResponseEntity<?> getPath(String source, String destination) {
		log.info("getPath");
		log.debug("source: {}", source);
		log.debug("destination: {}", destination);
		Map<String, List<String>> borderMap = countryPopularService.populate();
		List<String> route = findRoute(source, destination, borderMap);
		if (route == null) {
			return ResponseEntity.badRequest().body(null);
		} else {
			JSONObject response = new JSONObject();
			response.put("route", route);
			String json = response.toString(3);
			return ResponseEntity.ok(json);
		}
	}

	public List<String> findRoute(String source, String destination, Map<String, List<String>> borderMap) {
		Set<String> visitedFromSource = new HashSet<>();
		Set<String> visitedFromDest = new HashSet<>();

		Queue<String> queueFromSource = new LinkedList<>();
		Queue<String> queueFromDest = new LinkedList<>();

		Map<String, String> previousFromSource = new HashMap<>();
		Map<String, String> previousFromDest = new HashMap<>();

		// init of our finding operation
		queueFromSource.add(source);
		queueFromDest.add(destination);
		visitedFromSource.add(source);
		visitedFromDest.add(destination);
		previousFromSource.put(source, null);
		previousFromDest.put(destination, null);

		while (!queueFromSource.isEmpty() && !queueFromDest.isEmpty()) {
			String meetingPoint = exploreLevel(queueFromSource, visitedFromSource, visitedFromDest, previousFromSource,
					borderMap);
			if (meetingPoint != null) {
				return buildPath(meetingPoint, previousFromSource, previousFromDest);
			}

			// ------------------------------------
			meetingPoint = exploreLevel(queueFromDest, visitedFromDest, visitedFromSource, previousFromDest, borderMap);
			if (meetingPoint != null) {
				return buildPath(meetingPoint, previousFromSource, previousFromDest);
			}
		}

		return null;
	}

	private String exploreLevel(Queue<String> queue, Set<String> ourVisited, Set<String> theirVisited,
			Map<String, String> previous, Map<String, List<String>> borders) {
		int size = queue.size();

		for (int i = 0; i < size; i++) {
			String current = queue.remove();
			log.debug("current exploring {}", current);
			for (String neighbor : borders.getOrDefault(current, List.of())) {
				log.debug("neighbor {}", neighbor);
				if (!ourVisited.contains(neighbor)) {
					log.debug("new neighbor unlock pt1 {}", neighbor);
					ourVisited.add(neighbor);
					previous.put(neighbor, current);
					queue.add(neighbor);

					if (theirVisited.contains(neighbor)) {
						log.debug("common gruonds pt 2");
						return neighbor;
					}
				}
			}
		}
		return null;
	}

	private List<String> buildPath(String meetingPoint, Map<String, String> fromSource, Map<String, String> fromDest) {
		List<String> path = new ArrayList<>();

		String step = meetingPoint;
		while (step != null) {
			path.add(0, step); // keep pushing front cuz we met in the middle
			step = fromSource.get(step);
		}

		step = fromDest.get(meetingPoint);
		while (step != null) {
			path.add(step); // add to the end of list
			step = fromDest.get(step);
		}

		return path;
	}
}
