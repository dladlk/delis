export interface IModel {
  id: number;
  modified: Date;
  created: Date;
}

export class BaseModel implements IModel {
  id: number;
  modified: Date;
  created: Date;

  constructor(model ?: any) {
    if (model) {
      this.id = model.id;
      this.modified = new Date(model.modified);
      this.created = new Date(model.created);
    }
  }
}
