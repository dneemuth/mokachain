package mu.mvy.mokachain.common.statistics.aspect;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogExecutionTime {
	
	  private static final String LOG_MESSAGE_FORMAT = "%s.%s execution time: %dms";
	  private static final Logger LOG = LogManager.getLogger(LogExecutionTime.class);	 
	  
	  @Around("execution(* *(..)) && @annotation(mu.mvy.mokachain.common.statistics.LogMetrics)")
	  public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
	    StopWatch stopWatch = new StopWatch();
	    stopWatch.start();
	    Object retVal = joinPoint.proceed();
	    stopWatch.stop();
	    recordExecutionTime(joinPoint, stopWatch);
	    return retVal;
	  }
	  
	  private void recordExecutionTime(ProceedingJoinPoint joinPoint, StopWatch stopWatch) {
	    String logMessage = String.format(LOG_MESSAGE_FORMAT, joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName(), stopWatch.getTime());
	    LOG.debug(logMessage);
	    
	    System.out.println(logMessage);
	  }

}
