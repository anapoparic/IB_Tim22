import { Address } from '../../shared/model/address.model';
import { Photo } from '../../shared/model/photo.model';
import { Role } from './role.enum';

export interface User{
  id?: number;
  firstName?: string;
  lastName?: string;
  address?: Address;
  phone?: number;
  email?: string;
  password?: string;
  blocked?: boolean;
  verified?: boolean;
  active?: boolean;
  profilePicture?: Photo;
  role?: Role;
}
