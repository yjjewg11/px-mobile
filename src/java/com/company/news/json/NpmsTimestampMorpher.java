package com.company.news.json;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.object.AbstractObjectMorpher;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Morphs a String to a Date.<br>
 * 增加josn对Timestamp类型的支持
 */
public class NpmsTimestampMorpher extends AbstractObjectMorpher {
	
	private static Logger logger = LoggerFactory.getLogger(NpmsTimestampMorpher.class);
	private Timestamp defaultValue;
	private String[] formats;
	private boolean lenient;
	private Locale locale;

	/**
	 * @param formats
	 *            a list of formats this morpher supports.
	 */
	public NpmsTimestampMorpher(String[] formats) {
		this(formats, Locale.getDefault(), false);
	}

	/**
	 * @param formats
	 *            a list of formats this morpher supports.
	 * @param lenient
	 *            if the parsing should be lenient or not.
	 */
	public NpmsTimestampMorpher(String[] formats, boolean lenient) {
		this(formats, Locale.getDefault(), lenient);
	}

	/**
	 * @param formats
	 *            a list of formats this morpher supports.
	 * @param defaultValue
	 *            return value if the value to be morphed is null.
	 */
	public NpmsTimestampMorpher(String[] formats, Timestamp defaultValue) {
		this(formats, defaultValue, Locale.getDefault(), false);
	}

	/**
	 * @param formats
	 *            a list of formats this morpher supports.
	 * @param defaultValue
	 *            return value if the value to be morphed is null.
	 * @param locale
	 *            the Locale used to parse each format.
	 * @param lenient
	 *            if the parsing should be lenient or not.
	 */
	public NpmsTimestampMorpher(String[] formats, Timestamp defaultValue, Locale locale, boolean lenient) {
		super(true);
		if (formats == null || formats.length == 0) {
			throw new MorphException("invalid array of formats");
		}
		// should use defensive copying ?
		this.formats = formats;

		if (locale == null) {
			this.locale = Locale.getDefault();
		} else {
			this.locale = locale;
		}

		this.lenient = lenient;
		setDefaultValue(defaultValue);
	}

	/**
	 * @param formats
	 *            a list of formats this morpher supports.
	 * @param locale
	 *            the Locale used to parse each format.
	 */
	public NpmsTimestampMorpher(String[] formats, Locale locale) {
		this(formats, locale, false);
	}

	/**
	 * @param formats
	 *            a list of formats this morpher supports.
	 * @param locale
	 *            the Locale used to parse each format.
	 * @param lenient
	 *            if the parsing should be lenient or not.
	 */
	public NpmsTimestampMorpher(String[] formats, Locale locale, boolean lenient) {
		if (formats == null || formats.length == 0) {
			throw new MorphException("invalid array of formats");
		}
		// should use defensive copying ?
		this.formats = formats;

		if (locale == null) {
			this.locale = Locale.getDefault();
		} else {
			this.locale = locale;
		}

		this.lenient = lenient;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof NpmsTimestampMorpher)) {
			return false;
		}

		NpmsTimestampMorpher other = (NpmsTimestampMorpher) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(formats, other.formats);
		builder.append(locale, other.locale);
		builder.append(lenient, other.lenient);
		if (isUseDefault() && other.isUseDefault()) {
			builder.append(getDefaultValue(), other.getDefaultValue());
			return builder.isEquals();
		} else if (!isUseDefault() && !other.isUseDefault()) {
			return builder.isEquals();
		} else {
			return false;
		}
	}

	/**
	 * Returns the default value for this Morpher.
	 */
	public Timestamp getDefaultValue() {
		return (Timestamp) defaultValue.clone();
	}

	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(formats);
		builder.append(locale);
		builder.append(lenient);
		if (isUseDefault()) {
			builder.append(getDefaultValue());
		}
		return builder.toHashCode();
	}

	public Object morph(Object value) {
		if (value == null) {
			return null;
		}
		
		if(value instanceof String) {
			String valueStr = (String) value;
			if (StringUtils.isEmpty(valueStr)) {
				return null;
			}
		}

		if (Timestamp.class.isAssignableFrom(value.getClass())) {
			return (Timestamp) value;
		}

		if (!supports(value.getClass())) {
			throw new MorphException(value.getClass() + " is not supported");
		}

		String strValue = (String) value;
		SimpleDateFormat dateParser = null;

		for (int i = 0; i < formats.length; i++) {
			if (dateParser == null) {
				dateParser = new SimpleDateFormat(formats[i], locale);
			} else {
				dateParser.applyPattern(formats[i]);
			}
			dateParser.setLenient(lenient);
			try {
				return new Timestamp(dateParser.parse(strValue.toLowerCase()).getTime());
			} catch (ParseException pe) {
				logger.error("", pe);
			}
		}

		// unable to parse the date
		if (isUseDefault()) {
			return defaultValue;
		} else {
			throw new MorphException("Unable to parse the date " + value);
		}
	}

	public Class morphsTo() {
		return Timestamp.class;
	}

	/**
	 * Sets the defaultValue to use if the value to be morphed is null.
	 * 
	 * @param defaultValue
	 *            return value if the value to be morphed is null
	 */
	public void setDefaultValue(Timestamp defaultValue) {
		this.defaultValue = (Timestamp) defaultValue.clone();
	}

	public boolean supports(Class clazz) {
		return String.class.isAssignableFrom(clazz);
	}
}

