import { HttpClient } from "@angular/common/http";
import { inject } from "@angular/core";
import { BehaviorSubject, lastValueFrom, Subject } from "rxjs";
import { ConfirmOrder, MenuItem } from "./models";

const INIT_CO = {
  date: 0,
  order_id: '',
  payment_id: '',
  total: 0
}
export class RestaurantService {
 
  private http = inject(HttpClient)
  placedOrders = new BehaviorSubject<MenuItem[]>([])
  total = new BehaviorSubject<number>(0)
  confirmedOrder = new BehaviorSubject<ConfirmOrder>(INIT_CO)

  // TODO: Task 2.2
  // You change the method's signature but not the name
  getMenuItems(): Promise<MenuItem[]> {
    return lastValueFrom(this.http.get<MenuItem[]>('https://vttpcsf-production.up.railway.app/api/menu'));
  }

  sendPlacedOrders(selectedItems: MenuItem[], total: number) {
    this.placedOrders.next(selectedItems)
    this.total.next(total)
  }

  confirmOrder(co: ConfirmOrder) {
    this.confirmedOrder.next(co)
  }

  // TODO: Task 3.2
  confirmOrders(form: any, items: MenuItem[]): Promise<any> {
    console.info(">>> Sending order:", form.value, items)
    const body = {
      username: form.value['username'],
      password: form.value['password'],
      items: items
    }
    return lastValueFrom(this.http.post<any>('https://vttpcsf-production.up.railway.app/api/food_order', { body }));
  }
}
