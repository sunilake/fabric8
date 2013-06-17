package org.fusesource.camel.component.sap.util;

import static org.fusesource.camel.component.sap.model.rfc.RfcPackage.eNS_URI;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.fusesource.camel.component.sap.model.rfc.Destination;
import org.fusesource.camel.component.sap.model.rfc.RFC;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.Structure;
import org.fusesource.camel.component.sap.model.rfc.Table;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoRequest;
import com.sap.conn.jco.JCoResponse;
import com.sap.conn.jco.JCoTable;

public class RfcUtil {
	
	public static final String ROW = "row";

	public static final String GenNS_URI = "http://www.eclipse.org/emf/2002/GenModel";

	public static final String GenNS_DOCUMENTATION_KEY = "documentation";

	/**
	 * Details key for a parameter list entry annotation providing the description of the underlying JCo data field represented by the annotated parameter list entry. The details value is the description of the parameter list entry and its underlying JCo data field. 
	 */
	public static final String RfcNS_DESCRIPTION_KEY = "description";

	/**
	 * Details key for a parameter list entry annotation providing the fully-qualified Java classname of the annotated parameter list entry. The details value is the fully-qualified Java classname of the parameter list entry. 
	 */
	public static final String RfcNS_CLASS_NAME_OF_FIELD_KEY = "classNameOfField";

	/**
	 * Details key for a parameter list entry annotation indicating the table, structure or data element name of the underlying JCo data field represented by the annotated parameter list entry. The details value is the name of the table, structure or data element of the underlying JCo data field. The details values is <code>null</code> if the data element name is unavailable. 
	 */
	public static final String RfcNS_RECORD_TYPE_NAME_KEY = "recordTypeName";

	/**
	 * Details key for a parameter list entry annotation providing the JCo data type of the underlying data field represented by the annotated parameter list entry. The details value is the JCo data type of the underlying data field represented by the parameter list entry. 
	 */
	public static final String RfcNS_TYPE_KEY = "type";

	/**
	 * Details key for a parameter list entry annotation providing the String representation of the JCo data type of the underlying data field represented by the annotated parameter list entry. The details value is the String representation of the JCo data type of the underlying data field represented by the parameter list entry. 
	 */
	public static final String RfcNS_TYPE_AS_STRING_KEY = "typeAsString";

	/**
	 * Details key for a parameter list entry annotation providing the length of the underlying data field for Unicode layout represented by the annotated parameter list entry. The details value is the length of the underlying data field for Unicode layout represented by the parameter list entry. 
	 */
	public static final String RfcNS_UNICODE_BYTE_LENGTH_KEY = "unicodeByteLength";

	/**
	 * Details key for a parameter list entry annotation providing the byte length of the underlying data field for Non-Unicode layout represented by the annotated parameter list entry. The details value is the byte length of the underlying data field for Non-Unicode layout represented by the parameter list entry. 
	 */
	public static final String RfcNS_BYTE_LENGTH_KEY = "byteLength";

	/**
	 * Details key for a parameter list entry annotation providing the max length of the underlying data field represented by the annotated parameter list entry. The details value is the max length of the underlying data field represented by the parameter list entry.
	 * 
	 * <ul>
	 * <li><p>For character based data element types the length is the char length.
	 * <li><p>For <em><b>STRING</b></em> and <em><b>XSTRING</b></em> data element types the length is <em><b>0</b></em>.</li> 
	 * <li><p>For <em><b>STRUCTURE</b></em> and <em><b>TABLE</b></em> data element types the length is <em><b>0</b></em>.</li>
	 * <li><p>For numerical based data element types the length is the byte length.
	 * </ul> 
	 */
	public static final String RfcNS_LENGTH_KEY = "length";

	/**
	 * Details key for a parameter list entry annotation providing the number of decimals in the the underlying data field represented by the annotated parameter list entry. The details value is the number of decimals in the the underlying data field represented by the parameter list entry.
	 * 
	 * <p>The details value is possibly non-zero only for record entries whose underlying data field has the JCo data type <em><b>BCD</b></em> or <em><b>FLOAT</b></em>.
	 */
	public static final String RfcNS_DECIMALS_KEY = "decimals";

	/**
	 * Details key for a parameter list entry annotation providing the default value of the annotated parameter list entry. The details value is the default value of the parameter list entry.
	 */
	public static final String RfcNS_DEFAULT_KEY = "default";

	/**
	 * Details key for a parameter list entry annotation providing the field name in an underlying JCo structure or table represented by the annotated parameter list entry. The details value is the name of the field in the underlying JCo structure or table if the JCo parameter represented by the entry is defined by referencing that field. The details values is <code>null</code> otherwise. 
	 */
	public static final String RfcNS_RECORD_FIELD_NAME_KEY = "recordFieldName";

	/**
	 * Details key for a parameter list entry annotation indicating whether the annotated parameter list entry represents an underlying JCo <code>ABAP Object</code>. The details value is <code>true</code> if the parameter list entry represents an <code>ABAP Object</code>; <code>false</code> otherwise. 
	 */
	public static final String RfcNS_IS_ABAP_OBJECT_KEY = "isAbapObject";

	/**
	 * Details key for a parameter list entry annotation indicating whether the annotated parameter list entry represents an underlying <code>TYPE1</code> JCo structure. The details value is <code>true</code> if the parameter list entry represents a <code>TYPE1</code> structure; <code>false</code> otherwise. 
	 */
	public static final String RfcNS_IS_NESTED_TYPE1_STRUCTURE_KEY = "isNestedType1Structure";

	/**
	 * Details key for a parameter list entry annotation indicating whether the annotated parameter list entry is a <em><b>structure</b></em> type entry; i.e. is a {@link MappedRecord}. The details value is <code>true</code> if the parameter list entry is a {@link MappedRecord}; <code>false</code> otherwise. 
	 */
	public static final String RfcNS_IS_STRUCTURE_KEY = "isStructure";

	/**
	 * Details key for a parameter list entry annotation indicating whether the annotated parameter list entry is a <em><b>table</b></em> type entry; i.e. is an {@link IndexedRecord}. The details value is <code>true</code> if the parameter list entry is a {@link IndexedRecord}; <code>false</code> otherwise.	 
	 */
	public static final String RfcNS_IS_TABLE_KEY = "isTable";

	/**
	 * Details key for a parameter list entry annotation indicating whether the underlying data field represented by the annotated parameter list entry is an <em><b>import</b></em> parameter. The details value is <code>true</code> if the underlying data field is an import parameter; <code>false</code> otherwise.	 
	 */
	public static final String RfcNS_IS_IMPORT_KEY = "isImport";

	/**
	 * Details key for a parameter list entry annotation indicating whether the underlying data field represented by the annotated parameter list entry is an <em><b>export</b></em> parameter. The details value is <code>true</code> if the underlying data field is an export parameter; <code>false</code> otherwise.	 
	 */
	public static final String RfcNS_IS_EXPORT_KEY = "isExport";

	/**
	 * Details key for a parameter list entry annotation indicating whether the underlying data field represented by the annotated parameter list entry is a <em><b>changing</b></em> parameter. The details value is <code>true</code> if the underlying data field is a changing parameter; <code>false</code> otherwise.	 
	 */
	public static final String RfcNS_IS_CHANGING_KEY = "isChanging";

	/**
	 * Details key for a parameter list entry annotation indicating whether the underlying data field represented by the annotated parameter list entry is an <em><b>exception</b></em>. The details value is <code>true</code> if the underlying data field is an exception; <code>false</code> otherwise.	 
	 */
	public static final String RfcNS_IS_EXCEPTION_KEY = "isException";

	/**
	 * Details key for a parameter list entry annotation indicating whether the underlying data field represented by the annotated parameter list entry is an <em><b>optional</b></em> parameter. The details value is <code>true</code> if the underlying data field is an optional parameter; <code>false</code> otherwise.	 
	 */
	public static final String RfcNS_IS_OPTIONAL_KEY = "isOptional";

	private static final String STEXT_PARAM = "STEXT";
	private static final String FUNCTIONS_TABLE = "FUNCTIONS";
	private static final String FUNCNAME_PARAM = "FUNCNAME";
	private static final String RFC_FUNCTION_SEARCH_FUNCTION = "RFC_FUNCTION_SEARCH";
	private static final String GROUPNAME_PARAM = "GROUPNAME";

	private RfcUtil() {}
	
	public static Destination getDestination(String destinationName) {
		Destination destination = null;
		try {
			JCoDestination jcoDestination = JCoDestinationManager.getDestination(destinationName);
			destination = RfcFactory.eINSTANCE.createDestination();
			String repositoryName = jcoDestination.getRepository().getName();
			destination.setName(destinationName);
			destination.setRepositoryName(repositoryName);
		} catch (JCoException e) {
		}
		return destination; 
	}
	
	public static List<RFC> getRFCs(JCoDestination jcoDestination, String functionNameFilter, String groupNameFilter) {
		List<RFC> rfcs = new ArrayList<RFC>();
		try {
			JCoFunction jcoFunction = jcoDestination.getRepository().getFunction(RFC_FUNCTION_SEARCH_FUNCTION);
			jcoFunction.getImportParameterList().setValue(FUNCNAME_PARAM, functionNameFilter);
			jcoFunction.getImportParameterList().setValue(GROUPNAME_PARAM, groupNameFilter);
			jcoFunction.execute(jcoDestination);
			JCoTable sapFunctions = jcoFunction.getTableParameterList().getTable(FUNCTIONS_TABLE);
			
			if (sapFunctions.getNumRows() > 0) {
				sapFunctions.firstRow();
				do {
					RFC rfc = RfcFactory.eINSTANCE.createRFC();
					String functionName = sapFunctions.getString(FUNCNAME_PARAM);
					String groupName = sapFunctions.getString(GROUPNAME_PARAM);
					rfc.setName(functionName);
					rfc.setGroup(groupName);
					String functionDescription = sapFunctions.getString(STEXT_PARAM);
					rfc.setDescription(functionDescription);
					rfcs.add(rfc);
				} while (sapFunctions.nextRow());
			}
		} catch (JCoException e) {
			// Assume No Function Found
		}
		return rfcs;
	}
	
	public static Structure executeFunction(JCoDestination destination, String functionName, Structure request) {
		try {
			JCoRequest jcoRequest = destination.getRepository().getRequest(functionName);
			fillStructure(request, jcoRequest);
			JCoResponse jcoResponse = jcoRequest.execute(destination);
			Structure response = getResponse(destination, functionName);
			extractStructure(jcoResponse, response);
			return response;
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void beginTransaction(JCoDestination jcoDestination) throws JCoException {
		JCoContext.begin(jcoDestination);
	}
	
	public static void commitTransaction(JCoDestination jcoDestination) throws JCoException {
		JCoRequest request = jcoDestination.getRepository().getRequest("BAPI_TRANSACTION_COMMIT");
		request.setValue("WAIT", "X");
		request.execute(jcoDestination);
		JCoContext.end(jcoDestination);
	}
	
	public static void rollbackTransaction(JCoDestination jcoDestination) throws JCoException {
		JCoRequest request = jcoDestination.getRepository().getRequest("BAPI_TRANSACTION_ROLLBACK");
		request.execute(jcoDestination);
		JCoContext.end(jcoDestination);
	}
	
	public static Object getValue(EObject object, EStructuralFeature feature) {
		try {
			Object value = object.eGet(feature);
			if (value == null && feature instanceof EReference) {
				EClass eClass = ((EReference)feature).getEReferenceType();
				value = eClass.getEPackage().getEFactoryInstance().create(eClass);
				setValue(object, feature, value);
			}
			return value;
		} catch (Throwable exception) {
			return null;
		}
	}
	
	public static void setValue(EObject object, EStructuralFeature feature, Object value) {
		try {
			EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(object);
			if (editingDomain == null) {
				object.eSet(feature, value);
			} else {
				Command setCommand = SetCommand.create(editingDomain, object, feature, value);
				editingDomain.getCommandStack().execute(setCommand);
			}
		} catch (Throwable exception) {
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void extractStructure(JCoRecord jrecord, Structure eObject) {
		if (jrecord == null || eObject == null) 
			return;
		
		EClass eClass = eObject.eClass();
		JCoFieldIterator iterator = jrecord.getFieldIterator();
		while(iterator.hasNextField()) {
			JCoField field = iterator.nextField();
			EStructuralFeature feature = eClass.getEStructuralFeature(field.getName());
			Object value = getValue(eObject, feature);
			if (field.isStructure()) {
				if (value == null || !(value instanceof EObject))
					continue;
				extractStructure(field.getStructure(), (Structure)value);
			} else if (field.isTable()) {
				if (value == null || !(value instanceof EObject))
					continue;
				extractTable((JCoTable) field.getTable(), (Table<? extends Structure>) value);
			} else {
				setValue(eObject, feature, field.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void fillStructure(Structure eObject, JCoRecord jcoRecord) {
		if (jcoRecord == null || eObject == null) 
			return;

		EClass eClass = eObject.eClass();
		JCoFieldIterator iterator = jcoRecord.getFieldIterator();
		while(iterator.hasNextField()) {
			JCoField field = iterator.nextField();
			EStructuralFeature feature = eClass.getEStructuralFeature(field.getName());
			Object value = getValue(eObject, feature);
			if (field.isStructure()) {
				if (value == null || !(value instanceof Structure))
					continue;
				fillStructure((Structure)value, field.getStructure());
			} else if (field.isTable()) {
				if (value == null || !(value instanceof Table))
					continue;
				fillTable((Table<? extends Structure>) value, field.getTable());
			} else {
				field.setValue(value);
			}
		}
		
	}
	
	public static void extractTable(JCoTable jcoTable, Table<? extends Structure> table) {
		if (table == null || jcoTable == null) 
			return;
		
		EStructuralFeature feature = table.eClass().getEStructuralFeature(ROW);
		if (feature == null || !(feature instanceof EReference)) {
			return;
		}
		EClass rowType = ((EReference)feature).getEReferenceType();
		@SuppressWarnings("unchecked")
		EList<Structure> records = (EList<Structure>) getValue(table, feature);
		
		jcoTable.firstRow();
		for (int i = 0; i < jcoTable.getNumRows(); i++, jcoTable.nextRow()) {
			Structure newRow = (Structure) rowType.getEPackage().getEFactoryInstance().create(rowType);
			records.add(newRow);
			extractStructure(jcoTable, newRow);
		}
	}
	
	public static void fillTable(Table<? extends Structure> table, JCoTable jcoTable) {
		if (table == null || jcoTable == null) 
			return;
		
		EStructuralFeature feature = table.eClass().getEStructuralFeature(ROW);
		@SuppressWarnings("unchecked")
		EList<Structure> records = (EList<Structure>) getValue(table, feature);
		for (Structure row: records) {
			jcoTable.appendRow();
			fillStructure(row, (JCoRecord) jcoTable);
		}
	}
	
	public static Structure getRequest(String destinationName, String functionModuleName) {
		return	(Structure) getInstance(destinationName, functionModuleName, "Request");
	}
	
	public static Structure getResponse(String destinationName, String functionModuleName) {
		return (Structure) getInstance(destinationName, functionModuleName, "Response");
	}
	
	public static Structure getResponse(JCoDestination destination, String functionModuleName) {
		return (Structure) getInstance(destination, functionModuleName, "Response");
	}
	
	public static EObject getInstance(String destinationName, String functionModuleName, String eClassName) {
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationName);
			JCoRepository repository = destination.getRepository();
			String nsURI = eNS_URI + "/"  + repository.getName() + "/" + functionModuleName;
			
			EPackage ePackage = getEPackage(destinationName, nsURI);
			EClassifier classifier = ePackage.getEClassifier(eClassName);
			if (!(classifier instanceof EClass))
				return null;

			EClass eClass = (EClass) classifier;
			EObject eObject = ePackage.getEFactoryInstance().create(eClass);
			
			return eObject;
		} catch (JCoException e) {
			return null;
		}
	}

	public static EObject getInstance(JCoDestination destination, String functionModuleName, String eClassName) {
		try {
			JCoRepository repository = destination.getRepository();
			String nsURI = eNS_URI + "/"  + repository.getName() + "/" + functionModuleName;
			
			EPackage ePackage = getEPackage(destination, nsURI);
			EClassifier classifier = ePackage.getEClassifier(eClassName);
			if (!(classifier instanceof EClass))
				return null;

			EClass eClass = (EClass) classifier;
			EObject eObject = ePackage.getEFactoryInstance().create(eClass);
			
			return eObject;
		} catch (JCoException e) {
			return null;
		}
	}

	public static  EPackage getEPackage(String destinationName, String nsURI) {
		
		// Check whether the requested package has already been built.
		EPackage ePackage = (EPackage) EPackage.Registry.INSTANCE.get(nsURI);
		if (ePackage != null) {
			return ePackage;
		}

		// Retrieve the destination's repository.
		JCoRepository repository;
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationName);
			repository = destination.getRepository();
		} catch (JCoException e1) {
			return null;
		}
		
		// Check whether the requested package is defined by the destination's repository. 
		if (nsURI.startsWith(eNS_URI + "/" + repository.getName())) {
			
			// Extract the function module name from the URI.
			int prefixLength = eNS_URI.length() + repository.getName().length() + 2; // Length of "http://sap.jboss.org/<repo-name>/" prefix.
			String functionModuleName = nsURI.substring(prefixLength);
			
			// Retrieve the function module's meta-data. 
			JCoFunctionTemplate functionTemplate;
			try {
				functionTemplate = repository.getFunctionTemplate(functionModuleName);
			} catch (JCoException e) {
				return null;
			}
			JCoListMetaData importParameterListMetaData = functionTemplate.getImportParameterList();
			JCoListMetaData changingParameterListMetaData = functionTemplate.getChangingParameterList();
			JCoListMetaData tableParameterListMetaData = functionTemplate.getTableParameterList();
			JCoListMetaData exportParameterListMetaData = functionTemplate.getExportParameterList();

			// Create and initialize package
			EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
			ePackage = ecoreFactory.createEPackage();
			ePackage.setName(functionModuleName);
			ePackage.setNsPrefix(functionModuleName);
			ePackage.setNsURI(nsURI);
			
			// Create Request Class
			EClass requestClass = ecoreFactory.createEClass();
			ePackage.getEClassifiers().add(requestClass);
			requestClass.setName("Request");
			requestClass.getESuperTypes().add(RfcPackage.eINSTANCE.getStructure());
			addListMetaData(requestClass, importParameterListMetaData);
			addListMetaData(requestClass, changingParameterListMetaData);
			addListMetaData(requestClass, tableParameterListMetaData);
			addAnnotation(requestClass, GenNS_URI, GenNS_DOCUMENTATION_KEY, "Request for "
					+ functionModuleName);

			// Create Response Class
			EClass responseClass = ecoreFactory.createEClass();
			ePackage.getEClassifiers().add(responseClass);
			responseClass.setName("Response");
			responseClass.getESuperTypes().add(RfcPackage.eINSTANCE.getStructure());
			addListMetaData(responseClass, exportParameterListMetaData);
			addListMetaData(responseClass, changingParameterListMetaData);
			addListMetaData(responseClass, tableParameterListMetaData);
			addAnnotation(responseClass, GenNS_URI, GenNS_DOCUMENTATION_KEY, "Response for "
					+ functionModuleName);

			// Register Package
			EPackage.Registry.INSTANCE.put(nsURI, ePackage);
		}
		return ePackage;
	}

	public static  EPackage getEPackage(JCoDestination destination, String nsURI) {
		
		// Check whether the requested package has already been built.
		EPackage ePackage = (EPackage) EPackage.Registry.INSTANCE.get(nsURI);
		if (ePackage != null) {
			return ePackage;
		}

		// Retrieve the destination's repository.
		JCoRepository repository;
		try {
			repository = destination.getRepository();
		} catch (JCoException e1) {
			return null;
		}
		
		// Check whether the requested package is defined by the destination's repository. 
		if (nsURI.startsWith(eNS_URI + "/" + repository.getName())) {
			
			// Extract the function module name from the URI.
			int prefixLength = eNS_URI.length() + repository.getName().length() + 2; // Length of "http://sap.jboss.org/<repo-name>/" prefix.
			String functionModuleName = nsURI.substring(prefixLength);
			
			// Retrieve the function module's meta-data. 
			JCoFunctionTemplate functionTemplate;
			try {
				functionTemplate = repository.getFunctionTemplate(functionModuleName);
			} catch (JCoException e) {
				return null;
			}
			JCoListMetaData importParameterListMetaData = functionTemplate.getImportParameterList();
			JCoListMetaData changingParameterListMetaData = functionTemplate.getChangingParameterList();
			JCoListMetaData tableParameterListMetaData = functionTemplate.getTableParameterList();
			JCoListMetaData exportParameterListMetaData = functionTemplate.getExportParameterList();

			// Create and initialize package
			EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
			ePackage = ecoreFactory.createEPackage();
			ePackage.setName(functionModuleName);
			ePackage.setNsPrefix(functionModuleName);
			ePackage.setNsURI(nsURI);
			
			// Create Request Class
			EClass requestClass = ecoreFactory.createEClass();
			ePackage.getEClassifiers().add(requestClass);
			requestClass.setName("Request");
			requestClass.getESuperTypes().add(RfcPackage.eINSTANCE.getStructure());
			addListMetaData(requestClass, importParameterListMetaData);
			addListMetaData(requestClass, changingParameterListMetaData);
			addListMetaData(requestClass, tableParameterListMetaData);
			addAnnotation(requestClass, GenNS_URI, GenNS_DOCUMENTATION_KEY, "Request for "
					+ functionModuleName);

			// Create Response Class
			EClass responseClass = ecoreFactory.createEClass();
			ePackage.getEClassifiers().add(responseClass);
			responseClass.setName("Response");
			responseClass.getESuperTypes().add(RfcPackage.eINSTANCE.getStructure());
			addListMetaData(responseClass, exportParameterListMetaData);
			addListMetaData(responseClass, changingParameterListMetaData);
			addListMetaData(responseClass, tableParameterListMetaData);
			addAnnotation(responseClass, GenNS_URI, GenNS_DOCUMENTATION_KEY, "Response for "
					+ functionModuleName);

			// Register Package
			EPackage.Registry.INSTANCE.put(nsURI, ePackage);
		}
		return ePackage;
	}

	/**
	 * @param clazz
	 * @param jcoListMetaData
	 * @generated NOT
	 */
	public static void addAnnotation(EModelElement modelElement, String source, String key, String value) {
		EAnnotation annotation = modelElement.getEAnnotation(source);
		if (annotation == null) {
			annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource(source);
			annotation.setEModelElement(modelElement);
		}
		annotation.getDetails().put(key, value);
	}

	/**
	 * Populate the given {@link EClass} with structural features and annotations derived from the meta-data of the given {@link JCoListMetaData}.
	 * 
	 * @param eClass - the {@link EClass} populated with meta-data.
	 * @param jcoListMetaData - the {@link JCoListMetaData} from which the meta-data is derived.
	 */
	public static void addListMetaData(EClass eClass, JCoListMetaData jcoListMetaData) {
		if (jcoListMetaData == null)
			return;

		EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
		EPackage ePackage = eClass.getEPackage();
		for (int i = 0; i < jcoListMetaData.getFieldCount(); i++) {
			EStructuralFeature structuralFeature;
			if (jcoListMetaData.isStructure(i)) {
				JCoRecordMetaData jcoRecordMetaData = jcoListMetaData.getRecordMetaData(i);
				EClass structureClass = getStructureClass(ePackage, jcoRecordMetaData);
				EReference reference = ecoreFactory.createEReference();
				reference.setEType(structureClass);
				reference.setContainment(true);
				structuralFeature = reference;
				addAnnotation(structuralFeature, eNS_URI, RfcNS_CLASS_NAME_OF_FIELD_KEY,
						EObject.class.getName());
			} else if (jcoListMetaData.isTable(i)) {
				JCoRecordMetaData jcoRecordMetaData = jcoListMetaData.getRecordMetaData(i);
				EClass tableClass = getTableClass(ePackage, jcoRecordMetaData);
				EReference reference = ecoreFactory.createEReference();
				reference.setEType(tableClass);
				reference.setContainment(true);
				structuralFeature = reference;
				addAnnotation(structuralFeature, eNS_URI, RfcNS_CLASS_NAME_OF_FIELD_KEY,
						EObject.class.getName());
			} else {
				EAttribute attribute = ecoreFactory.createEAttribute();
				attribute.setEType(getEDataType(jcoListMetaData.getType(i)));
				structuralFeature = attribute;
				addAnnotation(structuralFeature, eNS_URI, RfcNS_CLASS_NAME_OF_FIELD_KEY,
						jcoListMetaData.getClassNameOfField(i));
			}
			structuralFeature.setName(jcoListMetaData.getName(i));
			if (!jcoListMetaData.isOptional(i))
				structuralFeature.setLowerBound(1);
			if (jcoListMetaData.getDefault(i) != null)
				structuralFeature.setDefaultValueLiteral(jcoListMetaData.getDefault(i));
			addAnnotation(structuralFeature, GenNS_URI, GenNS_DOCUMENTATION_KEY, jcoListMetaData.getDescription(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_DESCRIPTION_KEY,
					jcoListMetaData.getDescription(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_TYPE_KEY,
					Integer.toString(jcoListMetaData.getType(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_TYPE_AS_STRING_KEY,
					jcoListMetaData.getTypeAsString(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_RECORD_TYPE_NAME_KEY,
					jcoListMetaData.getRecordTypeName(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_LENGTH_KEY,
					Integer.toString(jcoListMetaData.getLength(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_BYTE_LENGTH_KEY,
					Integer.toString(jcoListMetaData.getByteLength(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_UNICODE_BYTE_LENGTH_KEY,
					Integer.toString(jcoListMetaData.getUnicodeByteLength(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_DECIMALS_KEY,
					Integer.toString(jcoListMetaData.getDecimals(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_DEFAULT_KEY, jcoListMetaData.getDefault(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_RECORD_FIELD_NAME_KEY,
					jcoListMetaData.getRecordFieldName(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_ABAP_OBJECT_KEY,
					Boolean.toString(jcoListMetaData.isAbapObject(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_NESTED_TYPE1_STRUCTURE_KEY,
					Boolean.toString(jcoListMetaData.isNestedType1Structure(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_STRUCTURE_KEY,
					Boolean.toString(jcoListMetaData.isStructure(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_TABLE_KEY,
					Boolean.toString(jcoListMetaData.isTable(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_IMPORT_KEY,
					Boolean.toString(jcoListMetaData.isImport(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_EXPORT_KEY,
					Boolean.toString(jcoListMetaData.isExport(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_CHANGING_KEY,
					Boolean.toString(jcoListMetaData.isChanging(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_OPTIONAL_KEY,
					Boolean.toString(jcoListMetaData.isOptional(i)));
			eClass.getEStructuralFeatures().add(structuralFeature);
		}
	}
	
	public static EClass getStructureClass(EPackage ePackage, JCoRecordMetaData jcoRecordMetaData) {
		
		// Check package to see if structure class has already been defined.
		EClassifier  structureClass = ePackage.getEClassifier(jcoRecordMetaData.getName());
		
		// Build structure class if not already built.
		if (!(structureClass instanceof EClass)) {

			structureClass = EcoreFactory.eINSTANCE.createEClass();
			ePackage.getEClassifiers().add(structureClass);
			structureClass.setName(jcoRecordMetaData.getName());
			addRecordMetaData(((EClass)structureClass), jcoRecordMetaData);
			((EClass)structureClass).getESuperTypes().add(RfcPackage.eINSTANCE.getStructure());
		}
		return (EClass) structureClass;
	}

	/**
	 * Create and return an {@link EClass} deriving from {@link IndexedRecord} and representing the {@link JCoTable}  
	 * @param ePackage
	 * @param jcoRecordMetaData
	 * @return
	 */
	public static EClass getTableClass(EPackage ePackage, JCoRecordMetaData jcoRecordMetaData) {
		
		// Check package to see if table class has already been defined.
		EClassifier  tableClass = ePackage.getEClassifier(jcoRecordMetaData.getName() + "_TABLE");
		
		// Build table class if not already built.
		if (!(tableClass instanceof EClass)) {
			
			// Create table class and add to package
			tableClass = EcoreFactory.eINSTANCE.createEClass();
			ePackage.getEClassifiers().add(tableClass);
			tableClass.setName(jcoRecordMetaData.getName() + "_TABLE");
			EClass rowStructureClass = getStructureClass(ePackage, jcoRecordMetaData);
			EReference reference = EcoreFactory.eINSTANCE.createEReference();
			reference.setEType(rowStructureClass);
			reference.setContainment(true);
			reference.setName(ROW);
			reference.setLowerBound(0);
			reference.setUpperBound(-1);
			((EClass)tableClass).getEStructuralFeatures().add(reference);
			((EClass)tableClass).getESuperTypes().add(RfcPackage.eINSTANCE.getTable());
		}
		return (EClass) tableClass;
	}

	/**
	 * Populate the given {@link EClass} with structural features and annotations derived from the meta-data of the given {@link JCoRecordMetaData}.
	 * 
	 * @param eClass - the {@link EClass} populated with meta-data.
	 * @param jcoRecordMetaData - the {@link JCoRecordMetaData} from which the meta-data is derived.
	 */
	public static void addRecordMetaData(EClass eClass, JCoRecordMetaData jcoRecordMetaData) {
		EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;	
		EPackage ePackage = eClass.getEPackage();
		for (int i = 0; i < jcoRecordMetaData.getFieldCount(); i++) {
			EStructuralFeature structuralFeature;
			if (jcoRecordMetaData.isStructure(i)) {
				JCoRecordMetaData jcoSubRecordMetaData = jcoRecordMetaData.getRecordMetaData(i);
				EClass structureClass = getStructureClass(ePackage, jcoSubRecordMetaData);
				EReference reference = ecoreFactory.createEReference();
				structuralFeature = reference;
				reference.setEType(structureClass);
				reference.setContainment(true);
				addAnnotation(structuralFeature, eNS_URI, RfcNS_CLASS_NAME_OF_FIELD_KEY, EObject.class.getName());
			} else if (jcoRecordMetaData.isTable(i)) {
				JCoRecordMetaData jcoSubRecordMetaData = jcoRecordMetaData.getRecordMetaData(i);
				EClass tableClass = getTableClass(ePackage, jcoSubRecordMetaData);
				EReference reference = ecoreFactory.createEReference();
				structuralFeature = reference;
				reference.setEType(tableClass);
				reference.setContainment(true);
				addAnnotation(structuralFeature, eNS_URI, RfcNS_CLASS_NAME_OF_FIELD_KEY, EObject.class.getName());
			} else {
				EAttribute attribute = ecoreFactory.createEAttribute();
				structuralFeature = attribute;
				attribute.setEType(getEDataType(jcoRecordMetaData.getType(i)));
				addAnnotation(structuralFeature, eNS_URI, RfcNS_CLASS_NAME_OF_FIELD_KEY, jcoRecordMetaData.getClassNameOfField(i));
			}
			structuralFeature.setName(jcoRecordMetaData.getName(i));
			addAnnotation(structuralFeature, GenNS_URI, GenNS_DOCUMENTATION_KEY, jcoRecordMetaData.getDescription(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_DESCRIPTION_KEY, jcoRecordMetaData.getDescription(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_TYPE_KEY, Integer.toString(jcoRecordMetaData.getType(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_TYPE_AS_STRING_KEY, jcoRecordMetaData.getTypeAsString(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_RECORD_TYPE_NAME_KEY, jcoRecordMetaData.getRecordTypeName(i));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_LENGTH_KEY, Integer.toString(jcoRecordMetaData.getLength(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_BYTE_LENGTH_KEY, Integer.toString(jcoRecordMetaData.getByteLength(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_UNICODE_BYTE_LENGTH_KEY, Integer.toString(jcoRecordMetaData.getUnicodeByteLength(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_DECIMALS_KEY, Integer.toString(jcoRecordMetaData.getDecimals(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_ABAP_OBJECT_KEY, Boolean.toString(jcoRecordMetaData.isAbapObject(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_NESTED_TYPE1_STRUCTURE_KEY, Boolean.toString(jcoRecordMetaData.isNestedType1Structure(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_STRUCTURE_KEY, Boolean.toString(jcoRecordMetaData.isStructure(i)));
			addAnnotation(structuralFeature, eNS_URI, RfcNS_IS_TABLE_KEY, Boolean.toString(jcoRecordMetaData.isTable(i)));
			eClass.getEStructuralFeatures().add(structuralFeature);
			
		}
	}

	/**
	 * Return the {@link EClassifier} corresponding to the given JCo data type.
	 * 
	 * @param jcoDataType - the JCo data type.
	 * @return the {@link EClassifier} corresponding to the given JCo data type.
	 */
	public static EClassifier getEDataType(int jcoDataType) {
		switch (jcoDataType) {

		case JCoMetaData.TYPE_INT:
		case JCoMetaData.TYPE_INT1:
		case JCoMetaData.TYPE_INT2:
			return EcorePackage.Literals.EINT;

		case JCoMetaData.TYPE_CHAR:
			return EcorePackage.Literals.ESTRING;

		case JCoMetaData.TYPE_NUM:
			return EcorePackage.Literals.ESTRING;

		case JCoMetaData.TYPE_BCD:
			return EcorePackage.Literals.EBIG_DECIMAL;

		case JCoMetaData.TYPE_DATE:
			return EcorePackage.Literals.EDATE;

		case JCoMetaData.TYPE_TIME:
			return EcorePackage.Literals.EDATE;

		case JCoMetaData.TYPE_FLOAT:
			return EcorePackage.Literals.EDOUBLE;

		case JCoMetaData.TYPE_BYTE:
			return EcorePackage.Literals.EBYTE_ARRAY;

		case JCoMetaData.TYPE_STRING:
			return EcorePackage.Literals.ESTRING;

		case JCoMetaData.TYPE_XSTRING:
			return EcorePackage.Literals.EBYTE_ARRAY;

		case JCoMetaData.TYPE_DECF16:
			return EcorePackage.Literals.EBIG_DECIMAL;

		case JCoMetaData.TYPE_DECF34:
			return EcorePackage.Literals.EBIG_DECIMAL;

		case JCoMetaData.TYPE_STRUCTURE:
			return EcorePackage.Literals.EOBJECT;

		case JCoMetaData.TYPE_TABLE:
			return EcorePackage.Literals.EOBJECT;

		default:
			return EcorePackage.Literals.EBYTE_ARRAY;
		}
	}
}
