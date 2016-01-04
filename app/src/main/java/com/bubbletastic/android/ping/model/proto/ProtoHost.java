// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: protos.proto at 13:1
package com.bubbletastic.android.ping.model.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import java.io.IOException;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.List;
import okio.ByteString;

public final class ProtoHost extends Message<ProtoHost, ProtoHost.Builder> {
  public static final ProtoAdapter<ProtoHost> ADAPTER = new ProtoAdapter_ProtoHost();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_HOST_NAME = "";

  public static final Boolean DEFAULT_SHOW_NOTIFICATION = false;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String host_name;

  @WireField(
      tag = 2,
      adapter = "com.bubbletastic.android.ping.model.proto.PingResult#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<PingResult> results;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#BOOL"
  )
  public final Boolean show_notification;

  public ProtoHost(String host_name, List<PingResult> results, Boolean show_notification) {
    this(host_name, results, show_notification, ByteString.EMPTY);
  }

  public ProtoHost(String host_name, List<PingResult> results, Boolean show_notification, ByteString unknownFields) {
    super(unknownFields);
    this.host_name = host_name;
    this.results = immutableCopyOf("results", results);
    this.show_notification = show_notification;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.host_name = host_name;
    builder.results = copyOf("results", results);
    builder.show_notification = show_notification;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ProtoHost)) return false;
    ProtoHost o = (ProtoHost) other;
    return equals(unknownFields(), o.unknownFields())
        && equals(host_name, o.host_name)
        && equals(results, o.results)
        && equals(show_notification, o.show_notification);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (host_name != null ? host_name.hashCode() : 0);
      result = result * 37 + (results != null ? results.hashCode() : 1);
      result = result * 37 + (show_notification != null ? show_notification.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (host_name != null) builder.append(", host_name=").append(host_name);
    if (results != null) builder.append(", results=").append(results);
    if (show_notification != null) builder.append(", show_notification=").append(show_notification);
    return builder.replace(0, 2, "ProtoHost{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<ProtoHost, Builder> {
    public String host_name;

    public List<PingResult> results;

    public Boolean show_notification;

    public Builder() {
      results = newMutableList();
    }

    public Builder host_name(String host_name) {
      this.host_name = host_name;
      return this;
    }

    public Builder results(List<PingResult> results) {
      checkElementsNotNull(results);
      this.results = results;
      return this;
    }

    public Builder show_notification(Boolean show_notification) {
      this.show_notification = show_notification;
      return this;
    }

    @Override
    public ProtoHost build() {
      return new ProtoHost(host_name, results, show_notification, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_ProtoHost extends ProtoAdapter<ProtoHost> {
    ProtoAdapter_ProtoHost() {
      super(FieldEncoding.LENGTH_DELIMITED, ProtoHost.class);
    }

    @Override
    public int encodedSize(ProtoHost value) {
      return (value.host_name != null ? ProtoAdapter.STRING.encodedSizeWithTag(1, value.host_name) : 0)
          + PingResult.ADAPTER.asRepeated().encodedSizeWithTag(2, value.results)
          + (value.show_notification != null ? ProtoAdapter.BOOL.encodedSizeWithTag(3, value.show_notification) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, ProtoHost value) throws IOException {
      if (value.host_name != null) ProtoAdapter.STRING.encodeWithTag(writer, 1, value.host_name);
      if (value.results != null) PingResult.ADAPTER.asRepeated().encodeWithTag(writer, 2, value.results);
      if (value.show_notification != null) ProtoAdapter.BOOL.encodeWithTag(writer, 3, value.show_notification);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public ProtoHost decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.host_name(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.results.add(PingResult.ADAPTER.decode(reader)); break;
          case 3: builder.show_notification(ProtoAdapter.BOOL.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public ProtoHost redact(ProtoHost value) {
      Builder builder = value.newBuilder();
      redactElements(builder.results, PingResult.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
