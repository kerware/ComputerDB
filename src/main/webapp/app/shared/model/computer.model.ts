import { ICompany } from 'app/shared/model/company.model';

export interface IComputer {
  id?: number;
  name?: string;
  introduced?: string | null;
  removed?: string | null;
  hardware?: number | null;
  software?: number | null;
  company?: ICompany | null;
}

export const defaultValue: Readonly<IComputer> = {};
