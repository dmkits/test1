/**
 *
 * @param printTableContentData
 * @created 2016-11-09
 */

/*
 * NEED LIBRARIES;
 <script src="/jslib/moment-with-locales.js"></script>
 <script src="/jslib/Numeral-js/min/numeral.min.js"></script>
 <script src="/jslib/Numeral-js/min/languages/ru-UA.min.js"></script>
 * */

var calcTableWidth = function (printTableContentData) {
    var col_width;
    var table_width = 0;
    var tableColumns = printTableContentData.columns;
    for (var col = 0; col < tableColumns.length; col++) {
        var tableColumnData = tableColumns[col];
        col_width = (tableColumnData.width != undefined) ? tableColumnData.width : 80;
        table_width += col_width;
    }
    return table_width;
};
/*
 tableColumns
 "columns":[
 {"data":"date", "name":"DATE","width":80, "type":"date"},
 {"data":"trailer", "name":"TRAILER", "width":80, "align":"center"},
 {"data":"sum","name":"SUM", "width":85, "type":"currency"},
 {"data":"distance", "name":"DISTANCE", "width":85, "type":"numeric"}
 ]
 tableData
 "data":[
 {
 "date":"03-10-2016", "trailer":"Y","sum": "20","distance":"500000"},
 ]
 */
var createPrintDetailTable = function (tableColumns, tableData, table_width) {
    var table = document.createElement('table');
    var thead = document.createElement("thead");
    var tbody = document.createElement('tbody');
    table.appendChild(thead);
    table.appendChild(tbody);

    var table_header_row = table.insertRow(0);
    for (var col = 0; col < tableColumns.length; col++) {
        var tableColumnData = tableColumns[col];
        var h_cell = document.createElement("TH");
        var col_width = (tableColumnData.width != undefined) ? tableColumnData.width : 80;
        h_cell.setAttribute("style", "width:" + col_width + "px");
        h_cell.innerHTML = tableColumnData.name;
        table_header_row.appendChild(h_cell);
    }
    table.setAttribute("style", "width:" + table_width + "px;");
    thead.appendChild(table_header_row);

    for (var row = 0; row < tableData.length; row++) {
        var dataItem = tableData[row];
        var tableRow = table.insertRow(-1);
        tbody.appendChild(tableRow);

        for (var col = 0; col < tableColumns.length; col++) {
            var tableCell = tableRow.insertCell(-1);
            var dataItemName = tableColumns[col].data;
            var data_type = tableColumns[col].type;
            var cellValue;
            if (dataItemName) {
                cellValue = formatValue(dataItem[dataItemName], data_type);
            }
            if (cellValue !== undefined && cellValue !== null) {
                tableCell.innerText = formatValue(cellValue, data_type);
                tableCell.setAttribute("style", createTableValueStyle(tableColumns[col]));
            }
        }
    }
    return table;
};
/*
 "header":[
 [
 {"id":"store","width":150, "type":"numeric", "label":"Номер склада:","value":6, "value_width":80, "align":"center" },
 {"width":350}
 ],
 [
 {"width":100},
 {"id":"bdate","width":200,  "type":"date", "label":"Нач.дата:","value":"2016-10-30", "value_width":80, "align":"center" },
 {"id":"edate","width":200, "type":"date", "label":"Конечн.дата:","value":"2016-11-06", "value_width":80, "align":"center" }
 ]
 ],
 */
var createDescriptiveTable = function (descriptiveData, table_width) {
    var div_content_all = document.createElement('div');
    div_content_all.setAttribute("id", "div_content_all");

    for (var j = 0; j < descriptiveData.length; j++) {

        var div_content_table = document.createElement('div');
        div_content_all.appendChild(div_content_table);

        var descript_table = document.createElement("table");
        div_content_table.appendChild(descript_table);
        descript_table.setAttribute("style", "width:" + table_width + "px; border:none");

        var tr = document.createElement("tr");
        tr.setAttribute("style", "border:none");
        descript_table.appendChild(tr);

        for (var i = 0; i < descriptiveData[j].length; i++) {
            var headerItemData = descriptiveData[j][i];

            var td = document.createElement("td");
            if (headerItemData.width !== undefined) {
                td.setAttribute("style", "width:" + headerItemData.width + "px; padding:0px; border:none");
            }
            var div = document.createElement("div");
            td.appendChild(div);
            var label;

            if (headerItemData.value !== undefined) {
                var valueSpan = document.createElement("div");
                if (headerItemData.id !== undefined) {
                    valueSpan.setAttribute("id", headerItemData.id);
                }
                valueSpan.setAttribute("style", "border: solid 1px;float:right; margin-left:5px; padding-right:5px; padding-left:5px;" + createTableValueStyle(headerItemData));

                valueSpan.innerText = formatValue(headerItemData.value, headerItemData.type, headerItemData.typeformat);
                if (label)
                    label.setAttribute("for", valueSpan.getAttribute("id"));
                div.appendChild(valueSpan);
            }
            if (headerItemData.label !== undefined) {
                label = document.createElement("div");
                label.innerText = headerItemData.label;
                label.setAttribute("style", "float: right;");
                div.appendChild(label);
            }
            tr.appendChild(td);
        }
    }
    return div_content_all;
};
var createTableValueStyle = function (ItemData) {
    var valueStyle = "";

    if (ItemData.value_width) {                                                         //only for div with label
        valueStyle += "width:" + ItemData.value_width + "px;";
    }
    if (ItemData.align) {
        valueStyle += "text-align:" + ItemData.align;
    }
    if (ItemData.type == "date") {
        if (!ItemData.align) valueStyle += "text-align:center; ";

    }
    if (ItemData.type == "numeric") {
        if (!ItemData.align) valueStyle += "text-align:right; ";
    }
    if (ItemData.type == "currency") {
        if (!ItemData.align) valueStyle += "text-align:right; ";
    }
    return valueStyle;
};
var formatValue = function (value, valueType, valueFormat) {
    numeral.language('ru-UA');

    if (!valueType) return value;
    if (valueType === "date") {
        if (!valueFormat) return moment(value).format("DD.MM.YYYY");
        else return moment(value).format(valueFormat);
    } else if (valueType === "numeric") {
        if (!valueFormat)
            return numeral(value).format('0,0');
        else return moment(value).format(valueFormat);
    }
    else if (valueType === "currency") {
        if (!valueFormat)
            return numeral(value).format('0,0.00');
        else return moment(value).format(valueFormat);
    }
    return value;
};