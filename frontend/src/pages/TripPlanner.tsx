import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Slider,
  Chip,
  Alert,
  CircularProgress,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { useNavigate } from 'react-router-dom';
import { api, endpoints, Country, TripInput, ProfileInput } from '../api/client';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';

const TripPlanner: React.FC = () => {
  const navigate = useNavigate();
  const [countries, setCountries] = useState<Country[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const [trips, setTrips] = useState<TripInput[]>([
    { countryCode: '', startDate: '', endDate: '' }
  ]);
  
  const [profile, setProfile] = useState<ProfileInput>({
    avgDailyMb: 100,
    avgDailyMin: 30,
    avgDailySms: 10,
  });

  const [userId, setUserId] = useState<number>(1);

  useEffect(() => {
    fetchCatalog();
  }, []);

  const fetchCatalog = async () => {
    try {
      setLoading(true);
      const response = await api.get(endpoints.catalog);
      setCountries(response.data.countries || []);
    } catch (err) {
      setError('Failed to fetch catalog data');
      console.error('Error fetching catalog:', err);
    } finally {
      setLoading(false);
    }
  };

  const addTrip = () => {
    setTrips([...trips, { countryCode: '', startDate: '', endDate: '' }]);
  };

  const removeTrip = (index: number) => {
    if (trips.length > 1) {
      setTrips(trips.filter((_, i) => i !== index));
    }
  };

  const updateTrip = (index: number, field: keyof TripInput, value: string) => {
    const newTrips = [...trips];
    newTrips[index] = { ...newTrips[index], [field]: value };
    setTrips(newTrips);
  };

  const handleSubmit = async () => {
    try {
      // Validate inputs
      if (trips.some(trip => !trip.countryCode || !trip.startDate || !trip.endDate)) {
        setError('Please fill in all trip details');
        return;
      }

      const simulationRequest = {
        userId,
        trips: trips.map(trip => ({
          countryCode: trip.countryCode,
          startDate: trip.startDate,
          endDate: trip.endDate,
        })),
        profile,
      };

      // Navigate to simulation page with data
      navigate('/simulation', { state: { simulationRequest } });
    } catch (err) {
      setError('Failed to process trip plan');
      console.error('Error processing trip plan:', err);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Box>
        <Typography variant="h4" gutterBottom>
          Plan Your Roaming Trip
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Grid container spacing={3}>
          {/* Trip Details */}
          <Grid size={{ xs: 12, md:8 }}>
            <Card>
              <CardContent>
                <Typography variant="h5" gutterBottom>
                  Trip Details
                </Typography>
                
                {trips.map((trip, index) => (
                  <Box key={index} sx={{ mb: 3, p: 2, border: '1px solid #e0e0e0', borderRadius: 1 }}>
                    <Grid container spacing={2} alignItems="center">
                      <Grid size={{ xs: 12, sm: 4 }}>
                        <FormControl fullWidth>
                          <InputLabel>Country</InputLabel>
                          <Select
                            value={trip.countryCode}
                            label="Country"
                            onChange={(e) => updateTrip(index, 'countryCode', e.target.value)}
                          >
                            {countries.map((country) => (
                              <MenuItem key={country.countryCode} value={country.countryCode}>
                                {country.countryName} ({country.region})
                              </MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </Grid>
                      
                      <Grid size={{ xs: 12, sm: 3 }}>
                        <TextField
                          fullWidth
                          label="Start Date"
                          type="date"
                          value={trip.startDate}
                          onChange={(e) => updateTrip(index, 'startDate', e.target.value)}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Grid>
                      
                      <Grid size={{ xs: 12, sm: 3 }}>
                        <TextField
                          fullWidth
                          label="End Date"
                          type="date"
                          value={trip.endDate}
                          onChange={(e) => updateTrip(index, 'endDate', e.target.value)}
                          InputLabelProps={{ shrink: true }}
                        />
                      </Grid>
                      
                      <Grid size={{ xs: 12, sm: 2 }}>
                        <Button
                          variant="outlined"
                          color="error"
                          onClick={() => removeTrip(index)}
                          disabled={trips.length === 1}
                          startIcon={<DeleteIcon />}
                        >
                          Remove
                        </Button>
                      </Grid>
                    </Grid>
                  </Box>
                ))}
                
                <Button
                  variant="outlined"
                  onClick={addTrip}
                  startIcon={<AddIcon />}
                  sx={{ mt: 2 }}
                >
                  Add Another Trip
                </Button>
              </CardContent>
            </Card>
          </Grid>

          {/* Usage Profile */}
          <Grid size={{ xs: 12, md: 4 }}>
            <Card>
              <CardContent>
                <Typography variant="h5" gutterBottom>
                  Usage Profile
                </Typography>
                
                <Box sx={{ mb: 3 }}>
                  <Typography gutterBottom>Daily Data Usage (MB)</Typography>
                  <Slider
                    value={profile.avgDailyMb}
                    onChange={(_, value) => setProfile({ ...profile, avgDailyMb: value as number })}
                    min={10}
                    max={1000}
                    step={10}
                    marks={[
                      { value: 10, label: '10MB' },
                      { value: 100, label: '100MB' },
                      { value: 1000, label: '1GB' },
                    ]}
                    valueLabelDisplay="auto"
                  />
                  <Typography variant="body2" color="text.secondary">
                    {profile.avgDailyMb} MB per day
                  </Typography>
                </Box>

                <Box sx={{ mb: 3 }}>
                  <Typography gutterBottom>Daily Voice Usage (minutes)</Typography>
                  <Slider
                    value={profile.avgDailyMin}
                    onChange={(_, value) => setProfile({ ...profile, avgDailyMin: value as number })}
                    min={0}
                    max={120}
                    step={5}
                    marks={[
                      { value: 0, label: '0min' },
                      { value: 30, label: '30min' },
                      { value: 120, label: '2h' },
                    ]}
                    valueLabelDisplay="auto"
                  />
                  <Typography variant="body2" color="text.secondary">
                    {profile.avgDailyMin} minutes per day
                  </Typography>
                </Box>

                <Box sx={{ mb: 3 }}>
                  <Typography gutterBottom>Daily SMS Usage</Typography>
                  <Slider
                    value={profile.avgDailySms}
                    onChange={(_, value) => setProfile({ ...profile, avgDailySms: value as number })}
                    min={0}
                    max={50}
                    step={1}
                    marks={[
                      { value: 0, label: '0' },
                      { value: 10, label: '10' },
                      { value: 50, label: '50' },
                    ]}
                    valueLabelDisplay="auto"
                  />
                  <Typography variant="body2" color="text.secondary">
                    {profile.avgDailySms} SMS per day
                  </Typography>
                </Box>

                <TextField
                  fullWidth
                  label="User ID"
                  type="number"
                  value={userId}
                  onChange={(e) => setUserId(Number(e.target.value))}
                  sx={{ mb: 2 }}
                />
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        <Box sx={{ mt: 3, textAlign: 'center' }}>
          <Button
            variant="contained"
            size="large"
            onClick={handleSubmit}
            disabled={trips.some(trip => !trip.countryCode || !trip.startDate || !trip.endDate)}
          >
            Plan My Trip
          </Button>
        </Box>
      </Box>
    </LocalizationProvider>
  );
};

export default TripPlanner;
