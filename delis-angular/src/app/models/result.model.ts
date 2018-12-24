import { IModel } from './base.model';

interface IBaseResult<T extends IModel> {
  code: number;
  errors: any[];
  status: boolean;
  data: T | IQueryData<T>;
}

export interface IQueryData<T extends IModel> {
  items: T[];
  pageNumber: number;
  pageSize: number;
  totalElement: number;

}

export interface IResult<T extends IModel> extends IBaseResult<T> {
  // noinspection JSAnnotator
  data: T;
}

export interface IResultList<T extends IModel> extends IBaseResult<T> {
  data: IQueryData<T>;
}
