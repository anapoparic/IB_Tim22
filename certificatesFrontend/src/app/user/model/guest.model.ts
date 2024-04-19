import { User } from "./user.model";

export interface Guest extends User{
    notificationEnable: boolean;
}