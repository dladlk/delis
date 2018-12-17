export class Notification {
  message: string;
  title: string;
  type: string;
  time ?: number;

  constructor(data: any) {
    this.message = data.message;
    this.title = data.title;
    this.type = data.type;
    this.time = data.time || 3000;
  }
}
