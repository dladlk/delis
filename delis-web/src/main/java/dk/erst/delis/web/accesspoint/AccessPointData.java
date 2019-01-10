package dk.erst.delis.web.accesspoint;

import dk.erst.delis.data.AccessPointType;
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
    private String certificate;
}
