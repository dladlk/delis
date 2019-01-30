package dk.erst.delis.web.accesspoint;

import dk.erst.delis.data.enums.access.AccessPointType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointData {
    private Long id;

    private String url;
    private AccessPointType type;
    private String certificate;
}
