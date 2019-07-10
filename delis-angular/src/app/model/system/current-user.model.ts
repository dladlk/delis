import { LoginModel } from "./login.model";

export class CurrentUserModel {

    username: string;
    firstName: string;
    lastName: string;
    role: string;
    organisation: string;
    lastLoginTime: Date;

    constructor(loginData: LoginModel) {
        this.username = loginData.username;
        this.firstName = loginData.firstName;
        this.lastName = loginData.lastName;
        this.role = loginData.role;
        this.organisation = loginData.organisation;
        this.lastLoginTime = loginData.lastLoginTime;
    }
}
