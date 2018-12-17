import {BaseModel, IModel} from './base.model';

export const ROLES = {

  MANAGER : 'ROLE_MANAGER',
  ADMIN : 'ROLE_ADMIN'
};

export interface IUser extends IModel {

  email: string;
  role: string;
  password: string;
}

export class User extends BaseModel implements IUser {

  email: string;
  role: string;
  password: string;

  constructor(model ?: any) {
    super(model);
    if (!model) {
      model = {};
    }

    this.email = model.email || null;
    this.role = model.role || null;
    this.password = model.password || null;
  }
}
