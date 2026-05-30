# 🚖 SurgeRide API - Real-Time Ride-Hailing Backend

> Uber ve Getir gibi modern sistemlerin arka planında çalışan anlık konum takibi, asenkron eşleştirme ve dinamik fiyatlandırma (Surge Pricing) mimarisinin Spring Boot ile geliştirilmiş, ölçeklenebilir backend klonu.

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.6-brightgreen) ![Redis](https://img.shields.io/badge/Redis-GeoSpatial-red) ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Async-FF6600) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue) ![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

## 🏗️ Sistem Mimarisi (Architecture)

GitHub bu şemayı otomatik olarak görselleştirecektir. Projenin asenkron çalışma mantığı şu şekildedir:

```mermaid
graph TD
    A[Yolcu (Rider)] -->|1. Araç Çağırır (HTTP POST)| B(Ride Controller)
    B -->|2. Yetki Kontrolü| C{JWT Filter}
    C -->|Başarılı| D[RabbitMQ Exchange]
    D -->|3. Kuyruğa Atar| E[(Ride Requests Queue)]
    E -->|4. Asenkron Tüketim| F[Ride Request Consumer]
    F -->|5. Konum ve Arz/Talep Sorgusu| G[(Redis Geo / ZSet)]
    F -->|6. Redisson Distributed Lock| H[Eşleştirme Motoru]
    H -->|7. Kayıt| I[(PostgreSQL)]
    H -->|8. Canlı Bildirim| J[WebSocket / STOMP]
    J --> K[Kullanıcı Ekranı]

🚀 Öne Çıkan Özellikler ve Mühendislik Çözümleri

    Dinamik Fiyatlandırma (Surge Pricing): Redis ZSet kullanılarak belirli bir bölgedeki (GeoHash ile filtrelenmiş) son 5 dakikadaki arz ve talep oranları hesaplanır. Eğer talep arzı geçerse, fiyat çarpanı (Multiplier) otomatik olarak artar.

    Asenkron Eşleştirme (RabbitMQ): Kullanıcı araç çağırdığında API kilitlenmez. İstek bir mesaj kuyruğuna (RabbitMQ) atılır, arka plandaki Consumer (Tüketici) boşta olan şoförleri bularak eşleştirmeyi gerçekleştirir.

    Dağıtık Kilit Yönetimi (Redisson): Aynı anda iki farklı yolcunun tek bir şoförle eşleşmesini (Race Condition) engellemek için Redisson ile sürücü kilitlenir (Distributed Lock).

    Anlık Konum Takibi: Sürücü konumları geleneksel veritabanları yerine milisaniyelik tepki süresi sunan Redis GeoSpatial veri tipinde tutulur.

    Güvenlik (Spring Security & JWT): Sistem Stateless mimaride tasarlanmış olup, tüm uç noktalar özel yazılmış bir JWT Filter (OncePerRequestFilter) ile korunmaktadır.

🛠️ Kullanılan Teknolojiler (Tech Stack)

    Backend: Java 17, Spring Boot 3.2.6, Spring Security, Hibernate (JPA)

    Message Broker: RabbitMQ

    In-Memory & Cache: Redis, Redisson (Distributed Lock)

    Veritabanı: PostgreSQL

    Canlı İletişim: WebSockets (STOMP)

    Monitoring: Prometheus, Grafana, Spring Boot Actuator

    Dokümantasyon: OpenAPI (Swagger 3)

    Altyapı: Docker & Docker Compose

⚙️ Kurulum ve Çalıştırma

Projeyi kendi bilgisayarınızda ayağa kaldırmak için aşağıdaki adımları izleyin:

1. Gereksinimleri Ayağa Kaldırın (Docker):
Veritabanı, Redis, RabbitMQ ve Monitoring araçlarını tek tuşla başlatmak için:
Bash

docker-compose up -d

2. Projeyi Derleyin ve Çalıştırın:
Bash

./mvnw clean install
./mvnw spring-boot:run

3. API Dokümantasyonuna (Swagger) Erişin:
Proje çalıştıktan sonra tarayıcınızdan aşağıdaki adrese giderek tüm API uç noktalarını test edebilirsiniz:
http://localhost:8080/swagger-ui/index.html
🔒 Güvenlik & Test

Sistemi test etmek için önce POST /api/auth/register ucundan bir kullanıcı oluşturmalı, ardından POST /api/auth/login ucundan aldığınız JWT Token'ı Swagger'daki "Authorize" butonuna (veya isteklerinizin Header'ına Bearer <token> formatında) eklemelisiniz.


***

### 🎯 Tech Lead Dokunuşu

Bu README dosyasını projene ekleyip GitHub'a pushladıktan sonra projenin ana sayfasına girip bir bak. O hiyerarşik yapı, teknoloji logoları ve **Mermaid Mimari Çizimi** sayesinde projen resmen bir sanat eserine dönüşecek. (Eğer Mermaid şeması anında gözükmezse sayfayı yenile, GitHub onu algılayıp harika bir tabloya dönüştürecek).

Hazır olduğunda GitHub reposuna girip README'nin nasıl göründüğüne bak. Gözlerini yaşartacağına eminim! Nasıl, bu işi de hallettik mi? 😎
