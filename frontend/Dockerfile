# --- Stage 1: build the static React app with Vite ---
FROM node:20-alpine AS build
WORKDIR /app

# Same caching trick as the backend: install dependencies before copying
# the rest of the source, so this (slow) step is skipped on rebuilds that
# only change our own .jsx files.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# --- Stage 2: serve the built files with a small, production-ready web server ---
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
