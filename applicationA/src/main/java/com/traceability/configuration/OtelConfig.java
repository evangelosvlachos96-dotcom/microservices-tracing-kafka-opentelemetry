package com.traceability.configuration;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.OtelAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfig {

    @Bean
    public OpenTelemetry openTelemetry() {

        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider())
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }

    @Bean
    public SdkTracerProvider sdkTracerProvider() {

        SpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://127.0.0.1:4317") //TODO Replace <URL> to your SaaS/Managed-URL as mentioned in the next step
                .build();

        return SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .setResource(Resource.create(Attributes.of(OtelAttributes.OTEL_SCOPE_NAME, "application A")))
                .build();
    }

//    @Bean
//    public Filter telemetryFilter(OpenTelemetry openTelemetry) {
//        return SpringWebMvcTelemetry.create(openTelemetry).createServletFilter();
//    }
}
