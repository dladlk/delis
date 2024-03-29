export class DetailsStateModel {

    currentIds: number[];
    nextIds: number[];
    nextUp: boolean;
    nextDown: boolean;
    currentId: number;
    skip: boolean;

    constructor() {
        this.currentIds = [];
        this.nextIds = [];
        this.nextUp = false;
        this.nextDown = false;
        this.currentId = null;
        this.skip = true;
    }
}
