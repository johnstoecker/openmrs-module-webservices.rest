/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource;

import org.openmrs.Patient;
import org.openmrs.activelist.Allergy;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Allergy, supporting standard CRUD operations
 */
@Resource("allergy")
@Handler(supports = Allergy.class, order = 0)
public class AllergyResource extends BaseActiveListItemResource<Allergy> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = super.getRepresentationDescription(rep);
			description.addProperty("allergyType");
			description.addProperty("reaction", Representation.REF);
			description.addProperty("severity");
			description.addProperty("allergen", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = super.getRepresentationDescription(rep);
			description.addProperty("allergyType");
			description.addProperty("reaction", Representation.DEFAULT);
			description.addProperty("severity");
			description.addProperty("allergen", Representation.DEFAULT);
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Allergy newDelegate() {
		return new Allergy();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("allergyType");
		description.addRequiredProperty("allergen");
		description.addProperty("reaction");
		description.addProperty("severity");
		
		return description;
	}
	
	/**
	 * Display string for allergy
	 * 
	 * @param allergy
	 * @return String ConceptName
	 */
	public String getDisplayString(Allergy allergy) {
		if (allergy.getAllergen() == null)
			return "";
		
		return allergy.getAllergen().getName().toString();
	}
	
	/**
	 * Annotated setter for allergen
	 * 
	 * @param allergen
	 * @param value
	 */
	@PropertySetter("allergen")
	public static void setAllergen(Allergy allergy, Object value) {
		allergy.setAllergen(Context.getConceptService().getConceptByUuid((String) value));
	}
	
	/**
	 * Gets allergies for a given patient (paged according to context if necessary)
	 * 
	 * @param patientUuid @see {@link PatientResource#getByUniqueId(String)} for interpretation
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	public SimpleObject getAllergiesByPatient(String patientUuid, RequestContext context) throws ResponseException {
		Patient patient = Context.getService(RestService.class).getResource(PatientResource.class)
		        .getByUniqueId(patientUuid);
		if (patient == null)
			throw new ObjectNotFoundException();
		return new NeedsPaging<Allergy>(Context.getPatientService().getAllergies(patient), context).toSimpleObject();
	}
	
}
