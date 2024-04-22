import {User} from "./user.model";

export interface UserReport {
  id?: number;
  reason?: string;
  reportedUser?: User;
  status?: boolean;
}
