package com.traceability.configuration;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
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
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
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

        // Create a Resource with service.name and other attributes
        Resource serviceResource = Resource.create(
                Attributes.of(
                        ResourceAttributes.SERVICE_NAME, "student-facturation",  // Application Name
                        ResourceAttributes.SERVICE_NAMESPACE, "student", // Optional: Namespace
                        ResourceAttributes.SERVICE_VERSION, "1.0.0",           // Optional: Version
                        AttributeKey.stringKey("environment"), "development"  // Optional: Custom Attribute
                )
        );

        return SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .setResource(Resource.getDefault().merge(serviceResource)) // Merge default attributes with custom ones
                .build();
    }

//    @Bean
//    public Filter telemetryFilter(OpenTelemetry openTelemetry) {
//        return SpringWebMvcTelemetry.create(openTelemetry).createServletFilter();
//    }
}
