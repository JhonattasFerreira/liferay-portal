/**
 * Append an item to a column and returns a new array of columns
 * @param {object} item 
 * @param {object[]} layoutColumns 
 * @param {number} targetColumnIndex
 * @return {object[]}
 * @review
 */
function appendItemToColumn(item, layoutColumns, targetColumnIndex) {
	const newLayoutColumns = layoutColumns;
	const targetColumn = newLayoutColumns[targetColumnIndex];

	targetColumn.splice(targetColumn.length, 0, item);

	return nextLayoutColumns;
}

/**
 * Removes following columns starting at position indicated
 * by startColumnIndex and returns a new array of columns
 * @param {object} layoutColumns
 * @param {number} startColumnIndex
 * @return {object}
 * @review
 */
function clearFollowingColumns(layoutColumns, startColumnIndex) {
	const nextLayoutColumns = layoutColumns.map(
		(layoutColumn) => [...layoutColumn]
	);

	for (let i = startColumnIndex + 1; i < nextLayoutColumns.length; i++) {
		nextLayoutColumns[i] = [];
	}

	return nextLayoutColumns;
}

/**
 * @param {object[]} layoutColumns
 * @param {number} columnIndex
 * @param {string} itemPlid
 * @return {boolean} Returns whether a column is child of an item or not
 * @review
 */
function columnIsItemChild(layoutColumns, columnIndex, itemPlid) {
	let isChild = false;
	const item = getItem(layoutColumns, itemPlid);

	if (item) {
		const itemColumnIndex = getItemColumnIndex(layoutColumns, itemPlid);
		isChild = item.active && (itemColumnIndex < columnIndex);
	}

	return isChild;
}

/**
 * Removes extra empty columns when there are more than three and returns
 * a new Array with the columns removed.
 * @param {object} layoutColumns
 * @private
 * @return {object}
 * @review
 */
function deleteEmptyColumns(layoutColumns) {
	const nextLayoutColumns = [...layoutColumns];

	for (let i = 3; (i < nextLayoutColumns.length) &&
		(nextLayoutColumns[i].length === 0); i++) {
		nextLayoutColumns.splice(i, 1);
	}

	return nextLayoutColumns;
}

/**
 * @param {object[]} layoutColumns
 * @param {string} sourceItemPlid
 * @param {string} targetItemPlid
 * @param {number} targetColumnIndex
 * @return {boolean} Returns whether a drop is valid or not
 * @review
 */
function dropIsValid(
	layoutColumns,
	sourceItemPlid,
	targetItemPlid,
	targetColumnIndex
) {
	const targetColumnIsChild = columnIsItemChild(
		layoutColumns,
		targetColumnIndex,
		sourceItemPlid
	);
	const targetEqualsSource = (sourceItemPlid === targetItemPlid);
	const targetExists = targetItemPlid || targetColumnIndex;

	return targetExists && !targetEqualsSource && !targetColumnIsChild;
}

/**
 * Return the active item of a given column
 * @param {object} layoutColumns
 * @param {number} columnIndex
 * @return {string}
 * @review
 */
function getColumnActiveItem(layoutColumns, columnIndex) {
	const column = layoutColumns[columnIndex];

	const activeItem = column.find(
		(item) => item.active
	);

	return (
		activeItem ? activeItem : null
	);
}

/**
 * Get last item of a column
 * @param {object[]} layoutColumns
 * @param {number} columnIndex
 * @return {object|undefined} Returns last item or
 * undefined if the column is empty
 * @review
 */
function getColumnLastItem(layoutColumns, columnIndex) {
	const column = layoutColumns[columnIndex];

	return column[column.length - 1];
}

/**
 * Returns an item from its plid
 * @param {object[]} layoutColumns
 * @param {string} itemPlid
 * @private
 * @return {object|null}
 * @review
 */
function getItem(layoutColumns, itemPlid) {
	let item = null;

	layoutColumns.forEach(
		(layoutColumn) => {
			item = item || layoutColumn.find(
				(_item) => _item.plid === itemPlid
			);
		}
	);

	return item;
}

/**
 * Get the container column of an item
 * @param {object[]} layoutColumns
 * @param {string} itemPlid
 * @return {object[]|null}
 * @review
 */
function getItemColumn(layoutColumns, itemPlid) {
	let column = null;

	layoutColumns.forEach(
		(layoutColumn) => {
			const item = layoutColumn.find(
				(_item) => _item.plid === itemPlid
			);

			if (item) {
				column = layoutColumn;
			}
		}
	);

	return column;
}

/**
 * Return the index of an item's column
 * @param {object[]} layoutColumns
 * @param {string} itemPlid
 * @return {number}
 * @review
 */
function getItemColumnIndex(layoutColumns, itemPlid) {
	const column = getItemColumn(layoutColumns, itemPlid);

	return layoutColumns.indexOf(column);
}

/**
 * @param {object[]} layoutColumns
 * @param {string} childItemPlid
 * @param {string} parentItemPlid
 * @return {boolean} Returns wheter an item is child of a given item
 * @review
 */
function itemIsParent(layoutColumns, childItemPlid, parentItemPlid) {
	const childItemColumnIndex = getItemColumnIndex(
		layoutColumns,
		childItemPlid
	);
	const parentItem = getItem(layoutColumns, parentItemPlid);
	const parentItemColumnIndex = getItemColumnIndex(
		layoutColumns,
		parentItemPlid
	);

	return parentItem.active && (childItemColumnIndex > parentItemColumnIndex);
}

/**
 * Removes an item from a column and returns a new array of columns
 * @param {string} itemPlid
 * @param {object[]} layoutColumns
 * @param {string} targetItemPlid
 * @return {object[]} new column array
 * @review
 */
function removeItem(itemPlid, layoutColumns) {
	const nextLayoutColumns = [...layoutColumns];

	const itemColumn = getItemColumn(nextLayoutColumns, itemPlid);

	if (itemColumn) {
		const itemIndex = itemColumn.findIndex(
			(item) => item.plid === itemPlid
		);
		const nextItemColumn = [...itemColumn];
		const nextItemColumnIndex = getItemColumnIndex(
			nextLayoutColumns,
			itemPlid
		);

		nextItemColumn.splice(itemIndex, 1);
		nextLayoutColumns[nextItemColumnIndex] = nextItemColumn;
	}

	return nextLayoutColumns;
}

export {
	appendItemToColumn,
	clearFollowingColumns,
	columnIsItemChild,
	deleteEmptyColumns,
	dropIsValid,
	getColumnActiveItem,
	getColumnLastItem,
	getItem,
	getItemColumn,
	getItemColumnIndex,
	itemIsParent,
	removeItem
};