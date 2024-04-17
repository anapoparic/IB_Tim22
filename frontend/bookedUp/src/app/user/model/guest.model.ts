import { Accommodation } from "../../accommodation/model/accommodation.model";
import { User } from "./user.model";

export interface Guest extends User{
    favourites: Accommodation[];
    notificationEnable: boolean;
}