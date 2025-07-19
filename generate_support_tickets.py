import csv
import random
import uuid
from datetime import datetime, timedelta

customers = [(f'cust{i:03}', f'Customer {i:03}') for i in range(1, 101)]
statuses = [
    'OPEN', 'IN_PROGRESS', 'WAITING_FOR_CUSTOMER',
    'WAITING_FOR_PAYMENT', 'RESOLVED', 'CLOSED', 'REJECTED'
]
now = datetime.now()

desc_templates = [
    "What is the status of my payment?",
    "Can you tell me when my transaction will be completed?",
    "Expected TAT for my transfer?",
    "I want to know if my payment is done.",
    "Why is my transaction delayed?",
    "When will I get my money?",
    "My payment is stuck, help!",
    "How long for the transfer to finish?",
    "Tat for my txn?",
    "I need help with my account.",
    "My name is spelled wrong, can you fix?",
    "I have a question about your service.",
    "Pls update me on my payment status.",
    "Wen will my trnsfr be done?",
    "Pourquoi mon paiement est-il en attente?",
    "¿Cuándo recibiré mi dinero?",
    "Mein Geld ist nicht angekommen.",
    "Moje platba je zpožděná, proč?",
    "My trnsction is not compltd yet.",
    "I want to chnge my email address."
]

def random_description():
    desc = random.choice(desc_templates)
    # Add some random noise
    if random.random() < 0.2:
        desc = desc.lower()
    if random.random() < 0.1:
        desc = desc.upper()
    if random.random() < 0.15:
        desc = desc.replace('payment', 'paymnt').replace('transaction', 'txn')
    if random.random() < 0.1:
        desc = desc.replace('status', 'sttus').replace('expected', 'expcted')
    if random.random() < 0.05:
        desc = desc + ' ...?'
    return desc

rows = []
for i in range(1000):
    cid, cname = random.choice(customers)
    ticket_id = str(uuid.uuid4())
    ticket_ts = now - timedelta(days=random.randint(0, 29), seconds=random.randint(0, 86400))
    status = random.choice(statuses)
    desc = random_description()
    last_updated = ticket_ts + timedelta(hours=random.randint(0, 72))
    rows.append([
        ticket_id, ticket_ts.isoformat(), status, desc, cname, last_updated.isoformat()
    ])

with open('src/test/resources/support_tickets.csv', 'w', newline='') as f:
    writer = csv.writer(f)
    writer.writerow([
        'ticketId', 'ticketTimestamp', 'status', 'description', 'customerName', 'lastUpdatedAt'
    ])
    writer.writerows(rows) 