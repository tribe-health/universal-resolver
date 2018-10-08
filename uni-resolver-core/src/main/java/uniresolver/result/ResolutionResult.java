package uniresolver.result;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import did.DIDDocument;

@JsonPropertyOrder({ "redirect", "didDocument", "resolverMetadata", "methodMetadata" })
public class ResolutionResult {

	public static final String MIME_TYPE = "application/json";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@JsonProperty
	private String redirect;

	@JsonProperty
	private DIDDocument didDocument;

	@JsonProperty
	private Map<String, Object> resolverMetadata;

	@JsonProperty
	private Map<String, Object> methodMetadata;

	private ResolutionResult() {

	}

	private ResolutionResult(String redirect, DIDDocument didDocument, Map<String, Object> resolverMetadata, Map<String, Object> methodMetadata) {

		this.redirect = redirect;
		this.didDocument = didDocument;
		this.resolverMetadata = resolverMetadata;
		this.methodMetadata = methodMetadata;
	}

	/*
	 * Factory methods
	 */

	public static ResolutionResult build(String redirect, DIDDocument didDocument, Map<String, Object> resolverMetadata, Map<String, Object> methodMetadata) {

		return new ResolutionResult(redirect, didDocument, resolverMetadata, methodMetadata);
	}

	public static ResolutionResult build(DIDDocument didDocument, Map<String, Object> resolverMetadata, Map<String, Object> methodMetadata) {

		return new ResolutionResult(null, didDocument, resolverMetadata, methodMetadata);
	}

	public static ResolutionResult build(DIDDocument didDocument) {

		return new ResolutionResult(null, didDocument, new HashMap<String, Object> (), new HashMap<String, Object> ());
	}

	public static ResolutionResult build() {

		return new ResolutionResult(null, DIDDocument.build(new HashMap<String, Object> ()), new HashMap<String, Object> (), new HashMap<String, Object> ());
	}

	/*
	 * Serialization
	 */

	public static ResolutionResult fromJson(String json) throws JsonParseException, JsonMappingException, IOException {

		return objectMapper.readValue(json, ResolutionResult.class);
	}

	public static ResolutionResult fromJson(Reader reader) throws JsonParseException, JsonMappingException, IOException {

		return objectMapper.readValue(reader, ResolutionResult.class);
	}

	public String toJson() throws JsonProcessingException {

		return objectMapper.writeValueAsString(this);
	}

	/*
	 * Getters and setters
	 */

	@JsonGetter
	public final String getRedirect() {

		return this.redirect;
	}

	@JsonSetter
	public final void setRedirect(String redirect) {

		this.redirect = redirect;
	}

	@JsonRawValue
	public final DIDDocument getDidDocument() {

		return this.didDocument;
	}

	public final void setDidDocument(DIDDocument didDocument) {

		this.didDocument = didDocument;
	}

	@JsonSetter
	public final void setDidDocument(Map<String, Object> jsonLdObject) {

		this.didDocument = DIDDocument.build(jsonLdObject);
	}

	@JsonGetter
	public final Map<String, Object> getResolverMetadata() {

		return this.resolverMetadata;
	}

	@JsonSetter
	public final void setResolverMetadata(Map<String, Object> resolverMetadata) {

		this.resolverMetadata = resolverMetadata;
	}

	@JsonGetter
	public final Map<String, Object> getMethodMetadata() {

		return this.methodMetadata;
	}

	@JsonSetter
	public final void setMethodMetadata(Map<String, Object> methodMetadata) {

		this.methodMetadata = methodMetadata;
	}
}
