package com.company.news.springMVC;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.object.AbstractObjectMorpher;

public class TimeMorpher  extends AbstractObjectMorpher{

	private String dateFormartStr ="yyyy-MM-dd";    
	private String format ;
	public TimeMorpher(){
		this.format = dateFormartStr;
	}
	
	public TimeMorpher(String formartstr){
		this.format = formartstr;
	}
	
	@Override
	public Object morph( Object value ) {

	      if( value == null ){
	         return null;
	      }

	      if( Timestamp.class.isAssignableFrom( value.getClass() ) ){
	         return (Timestamp) value;
	      }

	      if( !supports( value.getClass() ) ){
	         throw new MorphException( value.getClass() + " is not supported" );
	      }

	      String strValue = (String) value;
	      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
	        Date date = null;
	        try
	        {
	            date = simpleDateFormat.parse(strValue);
	        }
	        catch(ParseException e)
	        {
	            return null;
	        }
	        return new Timestamp(date.getTime());

	     
	}

	@Override
	public Class morphsTo() {
		return Timestamp.class;
	}

}
