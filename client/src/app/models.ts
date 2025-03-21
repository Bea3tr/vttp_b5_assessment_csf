// You may use this file to create any models
export interface MenuItem {
    id: string,
    name: string,
    description: string,
    price: number,
    quantity: number
}

export interface ConfirmOrder {
    date: number,
    order_id: string,
    payment_id: string,
    total: number
}