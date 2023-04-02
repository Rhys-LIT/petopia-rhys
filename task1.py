import requests


def main():
    send_my_get_request()
    send_my_post_request()


def send_my_get_request():
    headers = {
        "Content-Type": "application/json",
    }
    customer_id = 1
    url = f"http://localhost:8080/customers/{customer_id}"

    response = requests.get(url, headers=headers)

    expected_status_code = 200
    assert response.status_code == expected_status_code
    print(response.json())


def send_my_post_request():
    headers = {
        "Content-Type": "application/json",
    }
    url = "http://localhost:8080/customers"
    customer_json = {
        "id": 0,
        "firstName": "Jane",
        "lastName": "Doe",
        "email": "jane.doe@mia.com",
        "telephone": "0871234567",
        "streetAddress": "123 Main Street",
        "city": "Dublin",
        "country": "Ireland",
        "postcode": "D01 AB23"
    }

    response = requests.post(url, headers=headers, json=customer_json)

    expected_status_code = 200
    assert response.status_code == expected_status_code
    print(response.json())


if __name__ == '__main__':
    main()
