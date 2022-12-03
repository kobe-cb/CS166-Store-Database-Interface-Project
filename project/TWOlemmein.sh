#!/bin/bash

psql -h localhost -p $PGPORT "$USER"_DB
