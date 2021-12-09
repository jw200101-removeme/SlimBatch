package demo.mavenagile.slimbatch.sillybatch.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mavenagile.slimbatch.DBProcessor;

import demo.mavenagile.slimbatch.sillybatch.domain.SillyDO;
import demo.mavenagile.slimbatch.sillybatch.domain.SillyDataHelper;

public class SillyProcessor extends DBProcessor
{
	private Log LOGGER = LogFactory.getLog(SillyProcessor.class);
	private SillyDataHelper oSillyDataHelper;
	public static int oTotalProcessors = 0;
	private int oProcessorID;

	public SillyProcessor()
	{
		super();
		oProcessorID = ++oTotalProcessors;
		oSillyDataHelper = new SillyDataHelper();
	}

	@Override
	public Object processItem(Object varDO) throws Exception
	{
		SillyDO aDO = (SillyDO) varDO;
		if (oSillyDataHelper.processorSillyStep1(aDO) == false)
			return (null);

		if (oSillyDataHelper.processorSillyStep2(aDO) == false)
			return (null);

		if (oSillyDataHelper.processorSillyStep3(aDO) == false)
			return (null);

		if (oSillyDataHelper.processorSillyStep4(aDO) == false)
			return (null);

		if (oSillyDataHelper.processorSillyStep5(aDO) == false)
			return (null);
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("SillyProcessor[" + oProcessorID + "]:processItem process : " + aDO);
		return aDO;
	}
}
