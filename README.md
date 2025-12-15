# Calendar Reminder System

## Требования
- Docker
- Docker Compose

## Запуск проекта через Docker Compose
1. Клонировать репозиторий:

```bash
git clone https://github.com/shchff/calendar-reminder-system.git
cd calendar-reminder-system
```

2. Запустить приложение:

```bash
docker compose up --build
```

В первый раз может запускаться медленно, так как устанавливается JRE и подтягиваются все зависимости.

3. Приложение будет доступно по адресу: `http://localhost:8080`
