package eu.trentorise.smartcampus.protocolcarrier.custom;

public class FileRequestParam extends RequestParam {
	private String contentType;
	private String filename;
	private byte[] content;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String name) {
		this.filename = name;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
