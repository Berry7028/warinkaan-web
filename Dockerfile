FROM node:20-bookworm-slim

# Install OpenJDK and native build tools for node-pty
RUN apt-get update && apt-get install -y --no-install-recommends \
    openjdk-17-jdk-headless \
    python3 \
    make \
    g++ \
    locales \
    && rm -rf /var/lib/apt/lists/*

# Setup Japanese UTF-8 locale
RUN sed -i -e 's/# ja_JP.UTF-8 UTF-8/ja_JP.UTF-8 UTF-8/' /etc/locale.gen && \
    locale-gen ja_JP.UTF-8
ENV LANG=ja_JP.UTF-8
ENV LC_ALL=ja_JP.UTF-8

WORKDIR /app

# Install Node.js dependencies
COPY package*.json ./
RUN npm install --omit=dev

# Copy Java source code and compile
COPY src ./src
RUN mkdir -p bin && \
    javac -encoding UTF-8 -d bin \
      src/module-info.java \
      $(find src/warinkaan -name "*.java")

# Copy application files
COPY server.js ./
COPY public ./public

ENV PORT=3000
EXPOSE 3000

CMD ["node", "server.js"]
