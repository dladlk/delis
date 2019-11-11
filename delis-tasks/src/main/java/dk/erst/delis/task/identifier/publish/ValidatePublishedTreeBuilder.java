package dk.erst.delis.task.identifier.publish;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringExclude;

import dk.erst.delis.task.identifier.publish.ValidatePublishedService.IdentifierResult;
import dk.erst.delis.task.identifier.publish.ValidatePublishedService.ValidatePublishedResult;
import dk.erst.delis.task.identifier.publish.data.SmpDocumentIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpProcessIdentifier;
import dk.erst.delis.task.identifier.publish.data.SmpPublishData;
import dk.erst.delis.task.identifier.publish.data.SmpPublishServiceData;
import dk.erst.delis.task.identifier.publish.data.SmpServiceEndpointData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ValidatePublishedTreeBuilder {

	@Getter
	@ToString
	public static class TreeNode {
		private int level;
		private String node;
		@ToStringExclude
		private Set<TreeNode> children = new TreeSet<TreeNode>(new Comparator<TreeNode>() {
			@Override
			public int compare(TreeNode o1, TreeNode o2) {
				return o1.getNode().compareTo(o2.getNode());
			}
		});

		public TreeNode(String node) {
			this(node, 0);
		}

		public TreeNode(String node, int level) {
			this.node = node;
			this.level = level;
		}

		public TreeNode addChild(String childNode) {
			return addChild(new TreeNode(childNode, this.level + 1));
		}

		public TreeNode addChild(TreeNode child) {
			children.add(child);
			return child;
		}
	}

	@Getter
	@Setter
	private static class SmpFlatData {
		private SmpDocumentIdentifier documentIdentifier;
		private SmpProcessIdentifier processIdentifier;
		private SmpServiceEndpointData endpoint;

		public SmpFlatData(SmpServiceEndpointData endpoint, SmpProcessIdentifier processIdentifier, SmpDocumentIdentifier documentIdentifier) {
			this.endpoint = endpoint;
			this.processIdentifier = processIdentifier;
			this.documentIdentifier = documentIdentifier;
		}

		private static String byProcessIdentifierScheme(SmpFlatData d) {
			return d.getProcessIdentifier().getProcessIdentifierScheme();
		}

		private static String byProcessIdentifierValue(SmpFlatData d) {
			return d.getProcessIdentifier().getProcessIdentifierValue();
		}

		private static String byDocumentIdentifierScheme(SmpFlatData d) {
			return d.getDocumentIdentifier().getDocumentIdentifierScheme();
		}

		private static String byDocumentIdentifierValue(SmpFlatData d) {
			return d.getDocumentIdentifier().getDocumentIdentifierValue();
		}

		private static String byEndpointTransportProfile(SmpFlatData d) {
			return d.getEndpoint().getTransportProfile();
		}

		private static String byEndpointUrl(SmpFlatData d) {
			return d.getEndpoint().getUrl();
		}

		private static String byEndpointCertificate(SmpFlatData d) {
			return d.getEndpoint().getCertificateName();
		}
	}

	public static TreeNode buildExpectedTree(ValidatePublishedResult resultList) {
		SmpPublishData expected = resultList.getExpected();
		TreeNode tree = new TreeNode("Expected");
		if (expected != null) {
			Function<SmpPublishServiceData, String>[] profilesGroupByList = buildProfilesGroupByList();
			TreeNode profiles = tree.addChild("Profiles");
			List<SmpPublishServiceData> serviceList = expected.getServiceList();
			buildProfilesChildren(profiles, serviceList, 0, profilesGroupByList);

			Function<SmpServiceEndpointData, String>[] endpointsGroupByList = buildEndpointsGroupByList();
			TreeNode endpoints = tree.addChild("Endpoints");
			for (SmpPublishServiceData smpPublishServiceData : serviceList) {
				List<SmpServiceEndpointData> endpointList = smpPublishServiceData.getEndpoints();
				buildEndpointsChildren(endpoints, endpointList, 0, endpointsGroupByList);
			}
		}
		return tree;
	}

	public static void buildActualTreeList(ValidatePublishedResult resultList) {
		if (resultList.getIdentifierResultList() != null && !resultList.getIdentifierResultList().isEmpty()) {
			List<IdentifierResult> identifierResultList = resultList.getIdentifierResultList();
			for (IdentifierResult identifierResult : identifierResultList) {
				TreeNode tree = buildActualTree(identifierResult);
				identifierResult.setActualPublishedTree(tree);
			}
		}
	}

	public static TreeNode buildActualTree(IdentifierResult identifierResult) {
		TreeNode identifierTree;
		SmpPublishData published = identifierResult.getActualPublished();
		if (published != null) {
			identifierTree = new TreeNode("Published");
			List<SmpFlatData> flatList = new ArrayList<SmpFlatData>();
			for (SmpPublishServiceData sd : published.getServiceList()) {
				for (SmpServiceEndpointData e : sd.getEndpoints()) {
					flatList.add(new SmpFlatData(e, sd.getProcessIdentifier(), sd.getDocumentIdentifier()));
				}
			}

			Function<SmpFlatData, String>[] groupByList = buildIdentifierGroupByList();
			buildIdentifierChildren(identifierTree, flatList, 0, groupByList);
		} else {
			identifierTree = new TreeNode("Not published");
		}
		return identifierTree;

	}

	private static void buildProfilesChildren(TreeNode parent, List<SmpPublishServiceData> serviceList, int level, Function<SmpPublishServiceData, String>[] profilesGroupByList) {
		Map<String, List<SmpPublishServiceData>> grouped = serviceList.stream().collect(Collectors.groupingBy(profilesGroupByList[level]));
		for (String key : grouped.keySet()) {
			TreeNode child = parent.addChild(key);
			if (level + 1 < profilesGroupByList.length) {
				List<SmpPublishServiceData> childServiceList = grouped.get(key);
				buildProfilesChildren(child, childServiceList, level + 1, profilesGroupByList);
			}
		}
	}

	private static void buildEndpointsChildren(TreeNode parent, List<SmpServiceEndpointData> endpointList, int level, Function<SmpServiceEndpointData, String>[] groupByList) {
		Map<String, List<SmpServiceEndpointData>> grouped = endpointList.stream().collect(Collectors.groupingBy(groupByList[level]));
		for (String key : grouped.keySet()) {
			TreeNode child = parent.addChild(key);
			if (level + 1 < groupByList.length) {
				List<SmpServiceEndpointData> childServiceList = grouped.get(key);
				buildEndpointsChildren(child, childServiceList, level + 1, groupByList);
			}
		}
	}

	private static void buildIdentifierChildren(TreeNode parent, List<SmpFlatData> endpointList, int level, Function<SmpFlatData, String>[] groupByList) {
		Map<String, List<SmpFlatData>> grouped = endpointList.stream().collect(Collectors.groupingBy(groupByList[level]));
		for (String key : grouped.keySet()) {
			TreeNode child = parent.addChild(key);
			if (level + 1 < groupByList.length) {
				List<SmpFlatData> childServiceList = grouped.get(key);
				buildIdentifierChildren(child, childServiceList, level + 1, groupByList);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Function<SmpPublishServiceData, String>[] buildProfilesGroupByList() {
		Function<SmpPublishServiceData, String>[] classifier = new Function[] {

				(Function<SmpPublishServiceData, String>) SmpPublishServiceData::byProcessIdentifierScheme,

				(Function<SmpPublishServiceData, String>) SmpPublishServiceData::byProcessIdentifierValue,

				(Function<SmpPublishServiceData, String>) SmpPublishServiceData::byDocumentIdentifierScheme,

				(Function<SmpPublishServiceData, String>) SmpPublishServiceData::byDocumentIdentifierValue,

		};
		return classifier;
	}

	@SuppressWarnings("unchecked")
	private static Function<SmpServiceEndpointData, String>[] buildEndpointsGroupByList() {
		Function<SmpServiceEndpointData, String>[] classifier = new Function[] {

				(Function<SmpServiceEndpointData, String>) SmpServiceEndpointData::byEndpointCertificate,

				(Function<SmpServiceEndpointData, String>) SmpServiceEndpointData::byEndpointTransportProfile,

				(Function<SmpServiceEndpointData, String>) SmpServiceEndpointData::byEndpointUrl,

		};
		return classifier;
	}

	@SuppressWarnings("unchecked")
	private static Function<SmpFlatData, String>[] buildIdentifierGroupByList() {
		Function<SmpFlatData, String>[] classifier = new Function[] {

				(Function<SmpFlatData, String>) SmpFlatData::byEndpointCertificate,

				(Function<SmpFlatData, String>) SmpFlatData::byEndpointTransportProfile,

				(Function<SmpFlatData, String>) SmpFlatData::byEndpointUrl,

				(Function<SmpFlatData, String>) SmpFlatData::byProcessIdentifierScheme,

				(Function<SmpFlatData, String>) SmpFlatData::byProcessIdentifierValue,

				(Function<SmpFlatData, String>) SmpFlatData::byDocumentIdentifierScheme,

				(Function<SmpFlatData, String>) SmpFlatData::byDocumentIdentifierValue,

		};
		return classifier;
	}

}
