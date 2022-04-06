package no.bekk.bekkaway.order.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(SleuthWebProperties.TRACING_FILTER_ORDER + 1)
public class TraceIdHeaderFilter extends GenericFilterBean {
  public final static String HEADER_NAME = "X-Custom-TraceId";
  private final Tracer tracer;
  private static final Logger log = LoggerFactory.getLogger(TraceIdHeaderFilter.class);

  public TraceIdHeaderFilter(Tracer tracer) {
    this.tracer = tracer;
    log.info("Sending trace-id in responses as header {}", HEADER_NAME);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    Span currentSpan = this.tracer.currentSpan();

    if (currentSpan == null) {
      chain.doFilter(request, response);
      return;
    }

    ((HttpServletResponse) response).addHeader(HEADER_NAME, currentSpan.context().traceId());

    chain.doFilter(request, response);
  }
}
