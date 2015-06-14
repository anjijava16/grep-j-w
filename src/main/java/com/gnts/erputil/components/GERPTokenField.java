package com.gnts.erputil.components;

import org.vaadin.tokenfield.TokenField;
import com.gnts.erputil.constants.GERPConstants;

public class GERPTokenField extends TokenField {
	private static final long serialVersionUID = 1L;
	
	public GERPTokenField(String caption) {
		try {
			setWidth("110px");
			setCaption(caption);
			setInputPrompt(GERPConstants.selectDefault);
			setImmediate(true);
		}
		catch (Exception e) {
		}
		}
		
	

//

@SuppressWarnings("unchecked")
protected void rememberToken(String tokenId) {
	try{
    if (cb.addItem(getTokenCaption(tokenId)) != null) {
        // Sets the caption property, if used
        if (getTokenCaptionPropertyId() != null) {

            cb.getContainerProperty(tokenId, getTokenCaptionPropertyId())
                    .setValue(tokenId);

        }
    }
}catch(Exception e ){}
		}
}
