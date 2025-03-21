import { HttpClient } from "@angular/common/http";
import { inject } from "@angular/core";
import { BehaviorSubject, lastValueFrom } from "rxjs";
import { MenuItem } from "./models";

export class RestaurantService {

  private http = inject(HttpClient)
  placedOrders = new BehaviorSubject<MenuItem[]>([])
  total = new BehaviorSubject<number>(0)

  // TODO: Task 2.2
  // You change the method's signature but not the name
  getMenuItems(): Promise<MenuItem[]> {
    return lastValueFrom(this.http.get<MenuItem[]>('http://localhost:3000/api/menu'));
  }

  sendPlacedOrders(selectedItems: MenuItem[], total: number) {
    this.placedOrders.next(selectedItems)
    this.total.next(total)
  }

  // TODO: Task 3.2
  confirmOrders(form: any, items: MenuItem[]): Promise<any> {
    console.info(">>> Sending order:", form.value, items)
    const body = {
      username: form.value['username'],
      password: form.value['password'],
      items: items
    }
    return lastValueFrom(this.http.post<any>('http://localhost:3000/api/food_order', { body }));
  }
}
