package uniresolver.result;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uniresolver.ResolutionException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonPropertyOrder({ "didResolutionMetadata", "didDocumentStream", "didDocumentMetadata" })
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResolveRepresentationResult extends ResolveResult implements Result, StreamResult {

	private static final Logger log = LoggerFactory.getLogger(ResolveRepresentationResult.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@JsonProperty("didDocumentStream")
	private byte[] didDocumentStream;

	private ResolveRepresentationResult(Map<String, Object> didResolutionMetadata, byte[] didDocumentStream, Map<String, Object> didDocumentMetadata) {
		super(didResolutionMetadata, didDocumentMetadata);
		this.didDocumentStream = didDocumentStream != null ? didDocumentStream : new byte[0];
	}

	@Override
	@JsonIgnore
	public boolean isComplete() {
		return this.getContentType() != null && this.getDidDocumentStream() != null;
	}

	/*
	 * Factory methods
	 */

	@JsonCreator
	public static ResolveRepresentationResult build(@JsonProperty(value="didResolutionMetadata", required=false) Map<String, Object> didResolutionMetadata, @JsonProperty(value="didDocumentStream", required=false) byte[] didDocumentStream, @JsonProperty(value="didDocumentMetadata", required=false) Map<String, Object> didDocumentMetadata) {
		return new ResolveRepresentationResult(didResolutionMetadata, didDocumentStream, didDocumentMetadata);
	}

	public static ResolveRepresentationResult build() {
		return new ResolveRepresentationResult(new LinkedHashMap<>(), new byte[0], new LinkedHashMap<>());
	}

	public static ResolveRepresentationResult makeErrorResult(String error, String errorMessage, Map<String, Object> didResolutionMetadata, String contentType) {
		ResolveRepresentationResult resolveRepresentationResult = ResolveRepresentationResult.build();
		if (didResolutionMetadata != null) resolveRepresentationResult.getDidResolutionMetadata().putAll(didResolutionMetadata);
		resolveRepresentationResult.setError(error == null ? ERROR_INTERNALERROR : error);
		if (errorMessage != null) resolveRepresentationResult.setErrorMessage(errorMessage);
		resolveRepresentationResult.setContentType(contentType);
		resolveRepresentationResult.setDidDocumentStream(new byte[0]);
		if (log.isDebugEnabled()) log.debug("Created error resolve result: " + resolveRepresentationResult);
		return resolveRepresentationResult;
	}

	public static ResolveRepresentationResult makeErrorResult(ResolutionException ex, String contentType) {
		if (ex.getResolveRepresentationResult() != null && contentType.equals(ex.getResolveRepresentationResult().getContentType())) {
			return ex.getResolveRepresentationResult();
		}
		return makeErrorResult(ex.getError(), ex.getMessage(), ex.getDidResolutionMetadata(), contentType);
	}

	/*
	 * Serialization
	 */

	public static ResolveRepresentationResult fromJson(String json) throws IOException {
		return objectMapper.readValue(json, ResolveRepresentationResult.class);
	}

	public static ResolveRepresentationResult fromJson(Reader reader) throws IOException {
		return objectMapper.readValue(reader, ResolveRepresentationResult.class);
	}

	private static boolean isJson(byte[] bytes) {
		try {
			return objectMapper.getFactory().createParser(bytes).readValueAsTree() != null;
		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public Map<String, Object> toMap() {
		return objectMapper.convertValue(this, LinkedHashMap.class);
	}

	@Override
	public String toJson() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException("Cannot write JSON: " + ex.getMessage(), ex);
		}
	}

	/*
	 * Conversion
	 */

	public DereferenceResult toDereferenceResult() {
		DereferenceResult dereferenceResult = DereferenceResult.build();
		dereferenceResult.getDereferencingMetadata().putAll(this.getDidResolutionMetadata());
		dereferenceResult.setContentStream(this.getDidDocumentStream());
		dereferenceResult.getContentMetadata().putAll(this.getDidDocumentMetadata());
		return dereferenceResult;
	}

	@Override
	public void updateConversion() throws ResolutionException {

		if (this.resolveDataModelResult != null) {

			ResolveDataModelResult newResolveDataModelResult = Conversion.convertToResolveDataModelResult(this);
			this.resolveDataModelResult.setDidDocument(newResolveDataModelResult.getDidDocument());
		}
	}

	/*
	 * Content type methods
	 */

	@Override
	@JsonIgnore
	public String getContentType() {
		return this.getDidResolutionMetadata() == null ? null : (String) this.getDidResolutionMetadata().get("contentType");
	}

	@Override
	@JsonIgnore
	public void setContentType(String contentType) {
		if (this.getDidResolutionMetadata() == null) this.setDidResolutionMetadata(new LinkedHashMap<>());
		if (contentType != null)
			this.getDidResolutionMetadata().put("contentType", contentType);
		else
			this.getDidResolutionMetadata().remove("contentType");
	}

	/*
	 * Getters and setters
	 */

	@JsonIgnore
	public final byte[] getDidDocumentStream() {
		return this.didDocumentStream;
	}

	@JsonGetter("didDocumentStream")
	public final String getDidDocumentStreamAsString() {
		if (this.getDidDocumentStream() == null) {
			return null;
		} else {
			if (isJson(this.getDidDocumentStream())) {
				return new String(this.getDidDocumentStream(), StandardCharsets.UTF_8);
			} else {
				return Hex.encodeHexString(this.getDidDocumentStream());
			}
		}
	}

	@JsonIgnore
	public final void setDidDocumentStream(byte[] didDocumentStream) {
		this.didDocumentStream = didDocumentStream;
	}

	@JsonSetter("didDocumentStream")
	public final void setDidDocumentStreamAsString(String didDocumentStream) throws DecoderException {
		if (didDocumentStream == null) {
			this.setDidDocumentStream(null);
		} else {
			try {
				this.setDidDocumentStream(Hex.decodeHex(didDocumentStream));
			} catch (DecoderException ex) {
				this.setDidDocumentStream(didDocumentStream.getBytes(StandardCharsets.UTF_8));
			}
		}
	}

	@Override
	@JsonIgnore
	public final byte[] getContentStream() {
		return this.getDidDocumentStream();
	}

	@Override
	@JsonIgnore
	public final void setContentStream(byte[] stream) {
		this.setDidDocumentStream(stream);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {
		return this.toJson();
	}
}
