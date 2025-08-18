# Roaming Assistant Frontend

A modern React-based web application for planning and managing international roaming plans. This frontend integrates with the Spring Boot backend to provide a comprehensive roaming solution.

## Features

- **Trip Planner**: Multi-country trip planning with date selection
- **Usage Profile Management**: Customizable daily usage patterns for data, voice, and SMS
- **Smart Recommendations**: AI-powered roaming plan suggestions
- **Cost Simulation**: Detailed cost breakdown and comparison
- **Checkout Process**: Streamlined payment and activation flow
- **Responsive Design**: Mobile-first approach with Material-UI components

## Technology Stack

- **React 18** with TypeScript
- **Material-UI (MUI)** for component library
- **React Router** for navigation
- **Axios** for API communication
- **Date-fns** for date manipulation
- **Responsive design** with CSS Grid and Flexbox

## Project Structure

```
src/
├── api/
│   └── client.ts          # API client and type definitions
├── components/
│   └── Header.tsx         # Navigation header component
├── pages/
│   ├── TripPlanner.tsx    # Main trip planning interface
│   ├── Simulation.tsx     # Cost simulation results
│   ├── Recommendations.tsx # Smart recommendations display
│   └── Checkout.tsx       # Payment and confirmation flow
├── App.tsx                # Main application component
└── index.tsx              # Application entry point
```

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn
- Running Spring Boot backend on port 8000

### Installation

1. Install dependencies:

```bash
npm install
```

2. Start the development server:

```bash
npm start
```

3. Open [http://localhost:3000](http://localhost:3000) in your browser

### Build for Production

```bash
npm run build
```

## API Integration

The frontend communicates with the following backend endpoints:

- `GET /api/catalog` - Fetch countries, rates, and roaming packs
- `POST /api/simulate` - Run cost simulation
- `POST /api/recommendation` - Get smart recommendations
- `POST /api/checkout` - Process checkout

## Key Components

### TripPlanner

- Multi-country trip input with date pickers
- Usage profile sliders for data, voice, and SMS
- Real-time validation and error handling

### Simulation

- Cost comparison table
- Coverage and validity indicators
- Warning messages for potential issues

### Recommendations

- Top 3 ranked suggestions
- Detailed explanations and rationale
- Visual indicators for best choices

### Checkout

- 3-step checkout process
- Payment form with validation
- Order confirmation and next steps

## State Management

The application uses React's built-in state management:

- Local component state for form inputs
- Navigation state for passing data between pages
- API state for loading, error, and success states

## Styling

- Material-UI theme with custom color palette
- Responsive grid system for mobile and desktop
- Consistent spacing and typography
- Interactive hover states and animations

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Development

### Code Style

- TypeScript for type safety
- Functional components with hooks
- Consistent naming conventions
- Proper error handling

### Testing

```bash
npm test
```

### Linting

```bash
npm run lint
```

## Deployment

The application can be deployed to any static hosting service:

1. Build the production bundle:

```bash
npm run build
```

2. Deploy the `build/` folder to your hosting service

3. Configure environment variables if needed:

```bash
REACT_APP_API_BASE_URL=https://your-backend-url.com/api
```

## Troubleshooting

### Common Issues

1. **Backend Connection Error**: Ensure the Spring Boot backend is running on port 8000
2. **CORS Issues**: Check backend CORS configuration
3. **Build Errors**: Clear node_modules and reinstall dependencies

### Performance Tips

- Use React.memo for expensive components
- Implement proper loading states
- Optimize bundle size with code splitting
- Use lazy loading for routes

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is part of the Roaming Assistant solution.
