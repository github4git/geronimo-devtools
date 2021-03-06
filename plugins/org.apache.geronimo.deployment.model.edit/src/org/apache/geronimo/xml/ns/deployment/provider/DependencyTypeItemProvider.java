/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.xml.ns.deployment.provider;

import java.util.Collection;
import java.util.List;

import org.apache.geronimo.deployment.model.edit.GeronimoEMFEditPlugin;
import org.apache.geronimo.xml.ns.deployment.DependencyType;
import org.apache.geronimo.xml.ns.deployment.DeploymentPackage;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITableItemLabelProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a
 * {@link org.apache.geronimo.xml.ns.deployment.DependencyType} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated NOT
 *
 * @version $Rev$ $Date$
 */
public class DependencyTypeItemProvider extends ItemProviderAdapter implements
		IEditingDomainItemProvider, IStructuredItemContentProvider,
		ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource,
		ITableItemLabelProvider {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static final String copyright = "";

	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public DependencyTypeItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public List getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addGroupIdPropertyDescriptor(object);
			addTypePropertyDescriptor(object);
			addArtifactIdPropertyDescriptor(object);
			addVersionPropertyDescriptor(object);
			addUriPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Group Id feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addGroupIdPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory)
						.getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_DependencyType_groupId_feature"), getString(
						"_UI_PropertyDescriptor_description",
						"_UI_DependencyType_groupId_feature",
						"_UI_DependencyType_type"), DeploymentPackage.eINSTANCE
						.getDependencyType_GroupId(), true,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Type feature. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addTypePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory)
						.getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_DependencyType_type_feature"), getString(
						"_UI_PropertyDescriptor_description",
						"_UI_DependencyType_type_feature",
						"_UI_DependencyType_type"), DeploymentPackage.eINSTANCE
						.getDependencyType_Type(), true,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Artifact Id feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addArtifactIdPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory)
						.getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_DependencyType_artifactId_feature"), getString(
						"_UI_PropertyDescriptor_description",
						"_UI_DependencyType_artifactId_feature",
						"_UI_DependencyType_type"), DeploymentPackage.eINSTANCE
						.getDependencyType_ArtifactId(), true,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Version feature. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addVersionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory)
						.getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_DependencyType_version_feature"), getString(
						"_UI_PropertyDescriptor_description",
						"_UI_DependencyType_version_feature",
						"_UI_DependencyType_type"), DeploymentPackage.eINSTANCE
						.getDependencyType_Version(), true,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Uri feature. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addUriPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory)
						.getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_DependencyType_uri_feature"), getString(
						"_UI_PropertyDescriptor_description",
						"_UI_DependencyType_uri_feature",
						"_UI_DependencyType_type"), DeploymentPackage.eINSTANCE
						.getDependencyType_Uri(), true,
				ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This returns DependencyType.gif. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public Object getImage(Object object) {
		return getResourceLocator().getImage("full/obj16/DependencyType");
	}

	/**
	 * This returns the label text for the adapted class. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getText(Object object) {
		String label = ((DependencyType) object).getGroupId();
		return label == null || label.length() == 0 ? getString("_UI_DependencyType_type")
				: getString("_UI_DependencyType_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to
	 * update any cached children and by creating a viewer notification, which
	 * it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(DependencyType.class)) {
		case DeploymentPackage.DEPENDENCY_TYPE__GROUP_ID:
		case DeploymentPackage.DEPENDENCY_TYPE__TYPE:
		case DeploymentPackage.DEPENDENCY_TYPE__ARTIFACT_ID:
		case DeploymentPackage.DEPENDENCY_TYPE__VERSION:
		case DeploymentPackage.DEPENDENCY_TYPE__URI:
			fireNotifyChanged(new ViewerNotification(notification, notification
					.getNotifier(), false, true));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds to the collection of
	 * {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of
	 * the children that can be created under this object. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void collectNewChildDescriptors(Collection newChildDescriptors,
			Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

	/**
	 * Return the resource locator for this item provider's resources. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ResourceLocator getResourceLocator() {
		return GeronimoEMFEditPlugin.INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.edit.provider.ITableItemLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object object, int columnIndex) {
		DependencyType o = (DependencyType) object;
		switch (columnIndex) {
		case 0:
			if (o.eIsSet(DeploymentPackage.eINSTANCE.getDependencyType_Uri())) {
				return o.getUri();
			} else {
				return o.getGroupId() + "/" + o.getArtifactId() + "-"
						+ o.getVersion() + ".jar";
			}
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.edit.provider.ITableItemLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Object getColumnImage(Object object, int columnIndex) {
		if (columnIndex == 0) {
			return getImage(object);
		}
		return null;
	}

}
