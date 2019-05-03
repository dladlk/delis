package dk.erst.delis.pagefiltering.util;

import dk.erst.delis.pagefiltering.response.PageContainer;
import dk.erst.delis.pagefiltering.response.SortingDirection;

public class PaginatorUtil {

    public static final String FA_SORT = "fa-sort";
    public static final String FA_SORT_UP = "fa-sort-up";
    public static final String FA_SORT_DOWN = "fa-sort-down";
    public static final String ASC = "Asc";
    public static final String DESC = "Desc";

    public static String arrowStyle (PageContainer<?> pageContainer, String fieldName) {
        //${(#strings.equals(documentList.sortField,'createTime'))?((#strings.equals(documentList.sortDirection,'Desc'))?('fa-sort-down'):('fa-sort-up')):('fa-sort')}
        String result = FA_SORT;
        if (pageContainer.getSortField().equalsIgnoreCase(fieldName)) {
            if (pageContainer.getSortDirection() == SortingDirection.Asc) {
                result = FA_SORT_UP;
            } else {
                result = FA_SORT_DOWN;
            }
        }
        return result;
    }

    public static String sortFunc (PageContainer<?> pageContainer, String fieldName) {
        //'sortTableForm(\'documentTableForm\', \'createTime_'+ ${(#strings.equals(documentList.sortField,'createTime'))?((#strings.equals(documentList.sortDirection,'Desc'))?('Asc'):('Desc')):('Desc')} + '\')'
        String direction = DESC;
        if (pageContainer.getSortField().equalsIgnoreCase(fieldName)) {
            if (pageContainer.getSortDirection() == SortingDirection.Asc) {
                direction = DESC;
            } else {
                direction = ASC;
            }
        }
        String result = "sortTableForm('documentTableForm','" + fieldName + "_" + direction + "')";
        return result;
    }
}
