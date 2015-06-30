package com.company.news.commons.util;


public class XMLConstants {
	public static String getUploadXmlTemplate(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<AVMaterialEntity>" +
					"<ContentFile>" +
						"<Transtype>0</Transtype>" +
						"<FileGroup>" +
							"${files}" +
							"<GroupType>0</GroupType>" +
						"</FileGroup>" +
					"</ContentFile>" +
					"<EntityData>" +
						"<MaterialGuid>${uid}</MaterialGuid>" +
						"<MaterialCode>${taskname}</MaterialCode>" +
						"<MaterialName>${taskname}</MaterialName>" +
					"</EntityData>" +
				"</AVMaterialEntity>";
	}
}
