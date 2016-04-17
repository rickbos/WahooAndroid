package rick.bos.wahooandroid.service;

import com.wahoofitness.common.datatypes.Rate;

/**
 * Created by rick on 2016-04-10.
 */
public interface WahooServiceListener {
    public void wahooEvent( String str) ;
    public void wahooData(Rate rate) ;
}

