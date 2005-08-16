/**
 * Copyright 2004, 2005 The Apache Software Foundation or its licensors, as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.xml.ns.security;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.apache.geronimo.xml.ns.security.SecurityPackage
 * @generated
 */
public interface SecurityFactory extends EFactory{
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    SecurityFactory eINSTANCE = new org.apache.geronimo.xml.ns.security.impl.SecurityFactoryImpl();

    /**
     * Returns a new object of class '<em>Default Principal Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Default Principal Type</em>'.
     * @generated
     */
    DefaultPrincipalType createDefaultPrincipalType();

    /**
     * Returns a new object of class '<em>Description Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Description Type</em>'.
     * @generated
     */
    DescriptionType createDescriptionType();

    /**
     * Returns a new object of class '<em>Distinguished Name Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Distinguished Name Type</em>'.
     * @generated
     */
    DistinguishedNameType createDistinguishedNameType();

    /**
     * Returns a new object of class '<em>Document Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Document Root</em>'.
     * @generated
     */
    DocumentRoot createDocumentRoot();

    /**
     * Returns a new object of class '<em>Named Username Password Credential Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Named Username Password Credential Type</em>'.
     * @generated
     */
    NamedUsernamePasswordCredentialType createNamedUsernamePasswordCredentialType();

    /**
     * Returns a new object of class '<em>Principal Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Principal Type</em>'.
     * @generated
     */
    PrincipalType createPrincipalType();

    /**
     * Returns a new object of class '<em>Realm Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Realm Type</em>'.
     * @generated
     */
    RealmType createRealmType();

    /**
     * Returns a new object of class '<em>Role Mappings Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Role Mappings Type</em>'.
     * @generated
     */
    RoleMappingsType createRoleMappingsType();

    /**
     * Returns a new object of class '<em>Role Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Role Type</em>'.
     * @generated
     */
    RoleType createRoleType();

    /**
     * Returns a new object of class '<em>Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Type</em>'.
     * @generated
     */
    SecurityType createSecurityType();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    SecurityPackage getSecurityPackage();

} //SecurityFactory
