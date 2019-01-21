export class TableInfoModel {

    entityName: string;
    entityEnumInfo : [];

    constructor(model: any) {
        if (model) {
            this.entityName = model.entityName;
            this.entityEnumInfo = model.entityEnumInfo;
        }
    }
}