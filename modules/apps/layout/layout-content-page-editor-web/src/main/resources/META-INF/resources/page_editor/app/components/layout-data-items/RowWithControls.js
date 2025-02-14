/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import useSetRef from '../../../core/hooks/useSetRef';
import {
	LayoutDataPropTypes,
	getLayoutDataItemPropTypes,
} from '../../../prop-types/index';
import {LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS} from '../../config/constants/layoutDataFloatingToolbarButtons';
import {VIEWPORT_SIZES} from '../../config/constants/viewportSizes';
import selectCanUpdateItemConfiguration from '../../selectors/selectCanUpdateItemConfiguration';
import selectCanUpdatePageStructure from '../../selectors/selectCanUpdatePageStructure';
import selectShowFloatingToolbar from '../../selectors/selectShowFloatingToolbar';
import {useSelector} from '../../store/index';
import {getResponsiveConfig} from '../../utils/getResponsiveConfig';
import {useIsActive} from '../Controls';
import {ResizeContextProvider} from '../ResizeContext';
import Topper from '../Topper';
import FloatingToolbar from '../floating-toolbar/FloatingToolbar';
import Row from './Row';

const RowWithControls = React.forwardRef(
	({children, item, layoutData}, ref) => {
		const rowConfig = layoutData.items[item.itemId].config;
		const [resizing, setResizing] = useState(false);
		const [updatedLayoutData, setUpdatedLayoutData] = useState(null);
		const [customRow, setCustomRow] = useState(false);
		const isActive = useIsActive();

		const canUpdatePageStructure = useSelector(
			selectCanUpdatePageStructure
		);

		const selectedViewportSize = useSelector(
			(state) => state.selectedViewportSize
		);
		const showFloatingToolbar = useSelector(selectShowFloatingToolbar);

		const rowResponsiveConfig = getResponsiveConfig(
			rowConfig,
			selectedViewportSize
		);

		const [setRef, itemElement] = useSetRef(ref);

		const buttons = [];

		if (selectCanUpdateItemConfiguration) {
			buttons.push(LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.rowStyles);

			if (selectedViewportSize === VIEWPORT_SIZES.desktop) {
				buttons.push(
					LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.rowConfiguration
				);
			}
		}

		const {verticalAlignment} = rowResponsiveConfig;

		return (
			<Topper
				item={item}
				itemElement={itemElement}
				layoutData={layoutData}
			>
				<Row
					className={classNames({
						'align-bottom': verticalAlignment === 'bottom',
						'align-middle': verticalAlignment === 'middle',
						'page-editor__row': canUpdatePageStructure,
						'page-editor__row-overlay-grid': resizing,
					})}
					item={item}
					layoutData={layoutData}
					ref={setRef}
				>
					<ResizeContextProvider
						value={{
							customRow,
							resizing,
							setCustomRow,
							setResizing,
							setUpdatedLayoutData,
							updatedLayoutData,
						}}
					>
						{isActive(item.itemId) && showFloatingToolbar && (
							<FloatingToolbar
								buttons={buttons}
								item={item}
								itemElement={itemElement}
							/>
						)}
						{children}
					</ResizeContextProvider>
				</Row>
			</Topper>
		);
	}
);

RowWithControls.propTypes = {
	item: getLayoutDataItemPropTypes({
		config: PropTypes.shape({gutters: PropTypes.bool}),
	}).isRequired,
	layoutData: LayoutDataPropTypes.isRequired,
};

export default RowWithControls;
