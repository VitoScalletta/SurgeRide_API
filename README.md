# 🚗 SurgeRide API

> Spring Boot, Redis ve RabbitMQ ile geliştirilmiş; **anlık konum takibi** ve **dinamik fiyatlandırma (Surge Pricing)** yapabilen, ölçeklenebilir bir araç çağırma (Ride-Hailing) API mimarisi.

---

## 📋 İçindekiler

- [Proje Hakkında](#proje-hakkında)
- [Teknoloji Yığını](#teknoloji-yığını)
- [Özellikler](#özellikler)
- [Mimari](#mimari)
- [Kurulum](#kurulum)
- [API Dokümantasyonu](#api-dokümantasyonu)
- [İzleme & Metrikler](#i̇zleme--metrikler)

---

## Proje Hakkında

SurgeRide API, Uber/Bolt gibi araç çağırma servislerinin arka plan altyapısını simüle eden, üretim kalitesinde tasarlanmış bir backend projesidir. Sistem; gerçek zamanlı sürücü konum güncellemelerini, talep yoğunluğuna göre dinamik fiyat hesaplamayı (surge pricing) ve asenkron bildirim akışlarını yönetebilecek şekilde kurgulanmıştır.

---

## Teknoloji Yığını

| Katman | Teknoloji |
|---|---|
| Backend Framework | Java 17, Spring Boot 3.2.6 |
| Veritabanı | PostgreSQL 16 |
| Cache & Konum Takibi | Redis 7 + Redisson |
| Mesaj Kuyruğu | RabbitMQ 3 |
| Gerçek Zamanlı İletişim | WebSocket (STOMP) |
| Güvenlik | Spring Security + JWT (JJWT 0.11.5) |
| Coğrafi İndeksleme | GeoHash (ch.hsr) |
| İzleme | Prometheus + Grafana + Spring Actuator |
| API Dokümantasyonu | SpringDoc OpenAPI (Swagger UI) |
| Containerization | Docker Compose |

---

## Özellikler

- 🔐 **JWT tabanlı kimlik doğrulama** — Kayıt, giriş ve token yenileme
- 📍 **Gerçek zamanlı konum takibi** — GeoHash algoritması ile sürücü konumlarının Redis'te saklanması ve yakın sürücülerin bulunması
- 💰 **Surge Pricing motoru** — Bölgedeki anlık talep/arz oranına göre dinamik fiyat katsayısı hesaplama
- 📡 **WebSocket bildirimleri** — Yolcu ve sürücüye anlık durum güncellemeleri
- 📨 **Asenkron mesajlaşma** — RabbitMQ ile bağımsız servis iletişimi (yolculuk talepleri, bildirimler)
- 🔒 **Distributed Lock** — Redisson ile eşzamanlı surge pricing hesaplamalarında yarış koşullarının önlenmesi
- 📊 **Gözlemlenebilirlik** — Prometheus metrikleri ve Grafana dashboard'u

---

## Mimari

```
┌─────────────┐     REST/WS      ┌──────────────────────────┐
│   İstemci   │ ◄──────────────► │   Spring Boot API        │
└─────────────┘                  │                          │
                                 │  ┌──────────────────┐    │
                                 │  │  Security (JWT)  │    │
                                 │  └──────────────────┘    │
                                 │  ┌──────────────────┐    │
                                 │  │  Surge Pricing   │    │
                                 │  │     Engine       │    │
                                 │  └──────────────────┘    │
                                 │  ┌──────────────────┐    │
                                 │  │  Location Service│    │
                                 │  │  (GeoHash)       │    │
                                 │  └──────────────────┘    │
                                 └────────┬─────────────────┘
                                          │
              ┌───────────────────────────┼──────────────────┐
              ▼                           ▼                  ▼
       ┌─────────────┐           ┌──────────────┐   ┌──────────────┐
       │  PostgreSQL │           │    Redis      │   │  RabbitMQ   │
       │  (Kalıcı DB)│           │ (Cache+Konum) │   │  (Mesajlar) │
       └─────────────┘           └──────────────┘   └──────────────┘
```

---

## Kurulum

### Ön Gereksinimler

- Java 17+
- Docker & Docker Compose
- Maven

### 1. Repoyu Klonla

```bash
git clone https://github.com/VitoScalletta/SurgeRide_API.git
cd SurgeRide_API
```

### 2. Altyapıyı Başlat (Docker)

```bash
docker-compose up -d
```

Bu komut aşağıdaki servisleri ayağa kaldırır:

| Servis | Port |
|---|---|
| PostgreSQL | 5432 |
| Redis | 6379 |
| RabbitMQ | 5672 / 15672 (yönetim paneli) |
| Prometheus | 9090 |
| Grafana | 3000 |

### 3. Uygulamayı Çalıştır

```bash
./mvnw spring-boot:run
```

Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışır.

---

## API Dokümantasyonu

Uygulama çalışırken Swagger UI'a aşağıdaki adresten erişebilirsiniz:

```
http://localhost:8080/swagger-ui/index.html
```

---

## İzleme & Metrikler

| Araç | Adres | Kimlik Bilgisi |
|---|---|---|
| RabbitMQ Yönetim | http://localhost:15672 | guest / guest |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | admin / admin |

Spring Actuator metrikleri:
```
http://localhost:8080/actuator/prometheus
```

---

## Lisans

Bu proje eğitim amaçlı geliştirilmiştir.
