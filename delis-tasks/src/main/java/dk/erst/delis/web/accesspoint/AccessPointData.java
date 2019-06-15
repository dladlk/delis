package dk.erst.delis.web.accesspoint;

import dk.erst.delis.data.enums.access.AccessPointType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccessPointData {
    private Long id;

    private String url;
    private AccessPointType type;
    private String serviceDescription;
    private String technicalContactUrl;
    private String certificate;
}
