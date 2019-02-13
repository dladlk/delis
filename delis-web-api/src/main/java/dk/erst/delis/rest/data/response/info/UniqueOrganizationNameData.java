package dk.erst.delis.rest.data.response.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author funtusthan, created by 13.02.19
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UniqueOrganizationNameData {

    private List<String> UniqueOrganizationNames;
}
