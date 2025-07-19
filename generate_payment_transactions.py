import csv
import random
import uuid
from datetime import datetime, timedelta

customers = [(f'cust{i:03}', f'Customer {i:03}') for i in range(1, 101)]
statuses = [
    'CREATED', 'VALIDATED', 'COMPLIANCE_CHECKED',
    'SENT_TO_GATEWAY', 'PAID', 'FAILED', 'CANCELLED'
]
currencies = ['USD', 'EUR', 'INR', 'GBP', 'SGD']
modes = ['BANK_TRANSFER', 'CARD', 'UPI', 'PAYPAL']
gateways = ['Stripe', 'PayPal', 'Razorpay', 'Adyen', 'Square']
now = datetime.now()
rows = []
for i in range(10000):
    cid, cname = random.choice(customers)
    src_amt = round(random.uniform(10, 1000), 2)
    src_cur = random.choice(currencies)
    dst_amt = round(src_amt * random.uniform(0.95, 1.05), 2)
    dst_cur = random.choice([c for c in currencies if c != src_cur])
    mode = random.choice(modes)
    gw = random.choice(gateways)
    status = random.choice(statuses)
    tat = f'{random.randint(1, 5)} days'
    ts = now - timedelta(days=random.randint(0, 29), seconds=random.randint(0, 86400))
    rows.append([
        str(uuid.uuid4()), cid, cname, src_amt, src_cur, dst_amt, dst_cur,
        mode, gw, status, tat, ts.isoformat()
    ])

with open('src/test/resources/payment_transactions.csv', 'w', newline='') as f:
    writer = csv.writer(f)
    writer.writerow([
        'transactionId', 'customerId', 'customerName', 'sourceAmount', 'sourceCurrency',
        'destinationAmount', 'destinationCurrency', 'paymentMode', 'paymentGateway',
        'status', 'expectedTat', 'transactionTimestamp'
    ])
    writer.writerows(rows) 