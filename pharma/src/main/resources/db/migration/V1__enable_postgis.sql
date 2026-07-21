-- ============================================================
-- V1__enable_postgis.sql
-- الـ Migration الأولى: تفعيل PostGIS extension
--
-- ليه محتاجينها؟
-- الـ Pharmacy entity بتستخدم `geometry(Point, 4326)` لتخزين
-- الموقع الجغرافي (latitude/longitude) - PostGIS بيوفر ده.
--
-- ملاحظة: لازم تتنفذ قبل أي CREATE TABLE فيها geometry columns.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS postgis;
