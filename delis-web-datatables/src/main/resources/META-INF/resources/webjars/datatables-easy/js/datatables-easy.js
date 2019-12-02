;(function () {

    dataTablesEasy = {};

    dataTablesEasy.config = {
        tableClass: 'datatables-easy',
        withButtonsClass: 'dt-with-buttons'
    };

    dataTablesEasy.init = function () {
        $('table.' + dataTablesEasy.config.tableClass).each(function () {
            return configureDataTable(this)
        });
    };

    function configureDataTable(table) {
        var t = $(table);
        var pageData = getAttributeByPageDataContainer(t);
        if (!pageData) {
            console.log('Attribute with page data is not found on table ' + table);
            return false;
        }

        var tableForm = t.closest('form');
        if (!tableForm.length) {
            t.css('border-color', 'red');
            alert('Please wrap highlighted table with form');
            return;
        }

        var columnDefs = buildColumnDefs(t);
        var orderInfo = initOrders(columnDefs, pageData);

        var totalElements = pageData.totalElements;
        var size = pageData.size;
        var displayStart = pageData.displayStart;
        var displayEnd = pageData.displayEnd;
        if (pageData.filterMap === null) {
            pageData.filterMap = {};
        }

        var dataTablesSettings = {
            colReorder: true,
            responsive: false,
            pageLength: size,
            pagingType: "full",
            order: [orderInfo.col, orderInfo.dir],
            columnDefs: columnDefs,
            displayStart: displayStart - 1,
            buttons: [
                {
                    text: 'Clear',
                    action: function ( e, dt, node, config ) {
                        window.location.replace(window.location.pathname + '?clear=1');
                    }
                }
            ],
            dom: '<"d-flex justify-content-between">t<"row"><"d-flex justify-content-between"B<"mt-2"l>ip><"clear">',
            preDrawCallback: function (settings) {
                settings.oFeatures.bServerSide = "ssp";
                settings.bDestroying = true;
                settings.fnDisplayEnd = function () {
                    return displayEnd;
                };
                settings.fnRecordsTotal = function () {
                    return totalElements;
                };
                settings.fnRecordsDisplay = function () {
                    return totalElements;
                };
            }
        };

        renderFilters(t, columnDefs, pageData);

        if (t.hasClass(dataTablesEasy.config.withButtonsClass)) {
        	configButtons(dataTablesSettings);
        }

        var appDataTable = t
            .DataTable(dataTablesSettings)
            .on('order.dt', function (e, settings, order) {
                pageData.order = columnDefs[order[0].col].field + '_' + order[0].dir;
                dataTableRequest(this, pageData);
            })
            .on('length.dt', function (e, settings, len) {
                pageData.size = len;
                dataTableRequest(this, pageData);
            })
            .on('page.dt', function () {
                pageData.page = appDataTable.page.info().page + 1;
                dataTableRequest(this, pageData);
            });

        // overrideButtonColor();
        overrideClearButtonStyle();

        appDataTable.columns().every(function (i) {
            $('input', this.footer()).on('keypress', function (event) {
                if (event.keyCode === 13) {
                    doFilterAndRequest(this, pageData, columnDefs[i].field, this.value);
                }
            });
        });

        appDataTable.columns().every(function (i) {
            $('select', this.footer()).on('change', function () {
                var value = $.fn.dataTable.util.escapeRegex($(this).val());
                doFilterAndRequest(this, pageData, columnDefs[i].field, value);
            });
        });

        new $.fn.dataTable.FixedHeader(appDataTable, {
            headerOffset: 1
        });
    }

    function initOrders(columnDefs, pageData) {
        var orderColDir = pageData.order;
        var order = {};
        if (orderColDir !== null) {
            var sort = orderColDir.split('_');
            for (var index in columnDefs) {
                var dtInfo = columnDefs[index].field;
                var col = columnDefs[index].targets[0];
                if (dtInfo === sort[0]) {
                    order.col = col;
                    order.dir = sort[1];
                }
            }
        } else {
            order.col = 0;
            order.dir = "asc";
        }
        return order;
    }

    function renderFilters(t, columnDefs, pageData) {
        var thead = t.find('thead');
        var tfoot = t.find('tfoot');
        if (tfoot.length === 0) {
            t.append($('<tfoot></tfoot>'));
            tfoot = t.find('tfoot');
        }
        thead.find('tr').clone(true).appendTo(tfoot);
        
        tfoot.find('tr>th').each(function (i) {
            var title = $(this).text();
            if (i >= columnDefs.length) {
            	return;
            }
            var field = columnDefs[i].field;
            if (!field) {
            	return;
            }
            var filterType = columnDefs[i].type;
            var columnEnum = columnDefs[i].enum;
            var searchable = columnDefs[i].searchable;
            var searchValue = pageData.filterMap[field];

            if (searchable) {
                if (columnEnum !== undefined) {
                    selectFilter(this, columnEnum, searchValue !== undefined ? searchValue : null);
                } else if (filterType === 'date') {
                	$(this).html('<span class="form-control-date"/>');
                	dateFilter(this, field, searchValue, pageData);
                } else {
                	$(this).html('<input type="text" name="'+field+'" class="form-control" value="' + (searchValue !== undefined ? searchValue : '') + '" placeholder="' + title + '" />');                	
                }
            }
        });
    }
    
    var DATETIME_FORMAT = 'YYYY-MM-DDTHH:mm:ss';
    var SHORT_DATE_FORMAT = 'DD-MM-YY';
    
    function dateFilter(filterContainer, field, searchValue, pageData) {
        var drpSettings = {
            alwaysShowCalendars: true,
            ranges: initRanges(moment('2019-01-01'))
        };
        
    	if (searchValue !== undefined) {
	        var dateFromTo = searchValue.split('_');
	        drpSettings.startDate = moment(dateFromTo[0]);
	        drpSettings.endDate = moment(dateFromTo[1]);
    	}
        var daterangepicker = $(filterContainer).daterangepicker(drpSettings, function(start, end){
        	setDateInput(filterContainer, start, end);
        	var dateRangeValue = start.format(DATETIME_FORMAT) + '_' + end.format(DATETIME_FORMAT);
        	pageData.filterMap[field] = dateRangeValue; 
        	dataTableRequest(filterContainer, pageData);
        });
        
        if (drpSettings.startDate) {
        	setDateInput(filterContainer, drpSettings.startDate, drpSettings.endDate);
        }
    }
    
    function setDateInput(container, startDate, endDate) {
    	$(container).find('span').text(startDate.format(SHORT_DATE_FORMAT) + ' - ' + endDate.format(SHORT_DATE_FORMAT));
    }

    function selectFilter(ownerSelect, e, searchValue) {
        e = eval(e);
        var selectList = document.createElement("select");
        selectList.setAttribute('class', 'form-control');

        var option = document.createElement("option");
        option.value = "";
        option.text = "All";
        selectList.appendChild(option);
        
        for (var val in e) {
        	var optionObject = e[val];

        	var optionValue = optionObject;
        	var optionName = optionObject;
        	if (typeof optionObject !== 'string') {
        		if (optionObject.hasOwnProperty('name') && optionObject.hasOwnProperty('value')) {
	        		optionValue = optionObject.value;
	        		optionName = optionObject.name;
        		}
        	}

        	option = document.createElement("option");
	        option.value = optionValue;
	        option.text = optionName;
	        if (searchValue !== null && searchValue == optionValue) {
	        	option.selected = 'true';
	        }
	        selectList.appendChild(option);
        }

        ownerSelect.innerText = '';
        ownerSelect.appendChild(selectList);
    }

    function doFilterAndRequest(ownerEvent, pageData, field, value) {
        pageData.filterMap[field] = value;
        dataTableRequest(ownerEvent, pageData);
    }

    function configButtons(dataTablesSettings) {
        dataTablesSettings['buttons'] = [
            'colvis',
            'print',
            'copyHtml5',
            'csvHtml5',
            'excelHtml5',
            'pdfHtml5',
            {
                text: 'JSON',
                action: function (e, dt) {
                    var data = dt.buttons.exportData();
                    $.fn.dataTable.fileSave(
                        new Blob([JSON.stringify(data)]),
                        'export_data.json'
                    );
                }
            },
            {
                text: 'Clear',
                action: function () {
                    window.location.replace(window.location.href);
                }
            }
        ];
    }

    function overrideButtonColor() {
        var btns = document.querySelector('div.dt-buttons.btn-group');
        if (btns) {
            var kbButtons = btns.getElementsByTagName("button");
            for (var i = 0; i < kbButtons.length; i++) {
                kbButtons[i].style.backgroundColor = '#f8f9fa';
                kbButtons[i].style.color = 'black';
            }
        }
    }

    function overrideClearButtonStyle() {
        var btns = document.querySelector('div.dt-buttons.btn-group');
        if (btns) {
        	btns.className = 'dt-buttons'; // Remove btn-group to have small button
            var kbButtons = btns.getElementsByTagName("button");
            for (var i = 0; i < kbButtons.length; i++) {
                kbButtons[i].className = 'btn btn-outline-primary btn-sm mt-2';
            }
        }
    }

    function getAttributeByPageDataContainer(table) {
        var pageDataJson = table.attr('dt-page');
        if (pageDataJson) {
            return eval('(' + pageDataJson + ')');
        }
        return null;
    }

    var DEFAULT_COLUMN_DEF = {
        orderable: true,
        searchable: true
    };

    function buildColumnDefs(table) {
        var columnDefs = [];
        var columnIndex = 0;
        table.find('thead>tr>th').each(function () {
            var cd = {};
            $.each(this.attributes, function () {
                if (this.specified) {
                    if (startsWith(this.name, 'dt-')) {
                        cd[this.name.substring(3)] = buildColumnAttributeValue(this.name, this.value);
                        return;
                    }
                }
            });
            if (!cd.hasOwnProperty('field')) {
            	cd.orderable = false;
            	cd.searchable = false;
            } else {
	            if (!cd.hasOwnProperty('orderable')) {
	                cd.orderable = true;
	            }
	            if (!cd.hasOwnProperty('searchable')) {
	                cd.searchable = true;
	            }
            }
            cd.targets = [columnIndex];
            columnDefs.push(cd);
            columnIndex++;
        });
        return columnDefs;
    }

    function buildColumnAttributeValue(name, value) {
        if (value === 'true' || value === 'false') {
            return eval(value);
        }
        if (endsWith(name, '-json')) {
            return eval('(' + value + ')');
        }
        return value;
    }

    function dataTableRequest(owner, pageData) {

        if (pageData.page !== null) {
            $('<input>').attr({
                type: 'hidden',
                name: 'page',
                value: pageData.page
            }).appendTo(owner);
        }

        if (pageData.size !== null) {
            $('<input>').attr({
                type: 'hidden',
                name: 'size',
                value: pageData.size
            }).appendTo(owner);
        }

        if (pageData.order !== null) {
            $('<input>').attr({
                type: 'hidden',
                name: 'order',
                value: pageData.order
            }).appendTo(owner);
        }

        if (pageData.filterMap !== null) {

            var filter = 'filter_';

            var objectKeys = $.map(pageData.filterMap, function(value, key) {return key;});
            for (var key in objectKeys) {
                $('<input>').attr({
                    type: 'hidden',
                    name: filter + objectKeys[key],
                    value: pageData.filterMap[objectKeys[key]]
                }).appendTo(owner);
            }
        }

        var button = document.createElement("button");
        button.setAttribute('type', 'submit');
        button.style.visibility = 'hidden';
        owner.parentElement.appendChild(button);
        jQuery(button).trigger("click");
    }
    
    function initRanges(start) {
        return {
            'All': [start, moment()],
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        };
    }
    
    function startsWith(str, search, rawPos) {
        var pos = rawPos > 0 ? rawPos|0 : 0;
        return str.substring(pos, pos + search.length) === search;
    }
    
	function endsWith(str, search, this_len) {
		if (this_len === undefined || this_len > this.length) {
			this_len = str.length;
		}
		return str.substring(this_len - search.length, this_len) === search;
	}

}());
